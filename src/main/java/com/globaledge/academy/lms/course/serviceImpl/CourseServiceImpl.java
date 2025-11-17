package com.globaledge.academy.lms.course.serviceImpl;

import com.globaledge.academy.lms.assignment.dto.RuleExecutionResultDto;
import com.globaledge.academy.lms.assignment.entity.CourseAssignmentRule;
import com.globaledge.academy.lms.assignment.enums.ExecutionFrequency;
import com.globaledge.academy.lms.assignment.enums.RuleStatus;
import com.globaledge.academy.lms.assignment.repository.AssignmentRuleRepository;
import com.globaledge.academy.lms.assignment.service.RuleExecutionService;
import com.globaledge.academy.lms.course.dto.*;
import com.globaledge.academy.lms.course.entity.*;
import com.globaledge.academy.lms.course.enums.*;
import com.globaledge.academy.lms.course.repository.*;
import com.globaledge.academy.lms.course.service.CourseService;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;
    private final AssignmentRuleRepository assignmentRuleRepository;
    private final RuleExecutionService ruleExecutionService;


    @Override
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = mapToEntity(courseDto);
        // default to DRAFT if null
        if (course.getCourseStatus() == null) course.setCourseStatus(CourseStatus.DRAFT);
        course.setCreatedAt(LocalDateTime.now());
        // ensure relations point back
        if (course.getModules() != null) {
            course.getModules().forEach(m -> {
                m.setCourse(course);
                if (m.getModuleContents() != null) {
                    m.getModuleContents().forEach(c -> c.setCourseModule(m));
                }
            });
        }
        Course saved = courseRepository.save(course);
        return mapToDto(saved);
    }

    @Override
    public CourseDto updateCourse(Long courseId, CourseDto courseDto) {
        Course existing = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        // update allowed fields
        existing.setTitle(courseDto.getTitle());
        existing.setDescription(courseDto.getDescription());
        existing.setThumbnailUrl(courseDto.getThumbnailUrl());
        existing.setCourseCategory(courseDto.getCourseCategory());
        existing.setInstructor(courseDto.getInstructor());
        existing.setEstimatedDuration(courseDto.getEstimatedDuration());
        existing.setCreatedBy(courseDto.getCreatedBy()); // optional
        // If DTO contains status, allow changing it (or keep existing)
        if (courseDto.getCourseStatus() != null) {
            existing.setCourseStatus(courseDto.getCourseStatus());
            if (courseDto.getCourseStatus() == CourseStatus.PUBLISHED && existing.getPublishedAt() == null) {
                existing.setPublishedAt(LocalDateTime.now());
            } else if (courseDto.getCourseStatus() == CourseStatus.DRAFT) {
                existing.setPublishedAt(null);
            }
        }

        // Replace modules: simple approach - clear and re-add
        existing.getModules().clear();
        if (courseDto.getModules() != null) {
            for (CourseModuleDto moduleDto : courseDto.getModules()) {
                CourseModule module = modelMapper.map(moduleDto, CourseModule.class);
                module.setCourse(existing);
                if (moduleDto.getModuleContents() != null) {
                    module.setModuleContents(moduleDto.getModuleContents()
                            .stream()
                            .map(mcDto -> {
                                ModuleContent mc = modelMapper.map(mcDto, ModuleContent.class);
                                mc.setCourseModule(module);
                                return mc;
                            }).collect(Collectors.toList()));
                }
                existing.getModules().add(module);
            }
        }

        Course saved = courseRepository.save(existing);
        return mapToDto(saved);
    }

    @Override
    public CourseDto getCourseById(Long courseId) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        return mapToDto(c);
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> getCoursesByStatus(CourseStatus status) {
        return courseRepository.findByCourseStatus(status).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<CourseDto> getPublishedCourses() {
        return getCoursesByStatus(CourseStatus.PUBLISHED);
    }

    @Override
    public List<CourseDto> getCoursesByCategory(CourseCategory category) {
        return courseRepository.findByCourseCategory(category).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CourseDto publishCourse(Long courseId) {
        log.info("Publishing course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        if (course.getCourseStatus() == CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Course is already published");
        }

        course.setCourseStatus(CourseStatus.PUBLISHED);
        if (course.getPublishedAt() == null) {
            course.setPublishedAt(LocalDateTime.now());
        }

        Course saved = courseRepository.save(course);

        // ✅ ENHANCED: Track execution results
        int rulesExecutedCount = 0;
        int totalEnrollments = 0;

        try {
            List<CourseAssignmentRule> immediateRules = assignmentRuleRepository
                    .findByCourse_CourseId(courseId).stream()
                    .filter(rule -> rule.isActive()
                            && rule.getRuleStatus() == RuleStatus.ACTIVE
                            && rule.getExecutionFrequency() == ExecutionFrequency.IMMEDIATE)
                    .collect(Collectors.toList());

            rulesExecutedCount = immediateRules.size();

            // Execute immediate rules
            executeImmediateRules(courseId);

            // Calculate total enrollments created
            for (CourseAssignmentRule rule : immediateRules) {
                if (rule.getLastMatchedCount() != null) {
                    totalEnrollments += rule.getLastMatchedCount();
                }
            }
        } catch (Exception e) {
            log.error("Error during immediate rule execution: {}", e.getMessage());
        }

        log.info("Course published successfully: {}", courseId);

        // ✅ BUILD RESPONSE WITH SUMMARY
        CourseDto dto = mapToDto(saved);
        dto.setImmediateRulesExecuted(rulesExecutedCount);
        dto.setTotalEnrollmentsCreated(totalEnrollments);

        if (rulesExecutedCount > 0) {
            dto.setPublishSummary(String.format(
                    "Course published successfully. %d rule(s) executed, %d employee(s) enrolled.",
                    rulesExecutedCount, totalEnrollments
            ));
        } else {
            dto.setPublishSummary(
                    "Course published successfully. No immediate enrollment rules found. " +
                            "Create rules with IMMEDIATE frequency to auto-enroll employees on publish."
            );
        }

        return dto;
    }


    private void executeImmediateRules(Long courseId) {
        try {
            log.info("=== Executing Immediate Assignment Rules for Course: {} ===", courseId);

            List<CourseAssignmentRule> immediateRules = assignmentRuleRepository
                    .findByCourse_CourseId(courseId).stream()
                    .filter(rule -> rule.isActive()
                            && rule.getRuleStatus() == RuleStatus.ACTIVE
                            && rule.getExecutionFrequency() == ExecutionFrequency.IMMEDIATE)
                    .collect(Collectors.toList());

            if (immediateRules.isEmpty()) {
                log.info("No immediate rules found for course: {}", courseId);
                log.info("To auto-enroll employees, create rules with IMMEDIATE frequency before publishing");
                return;
            }

            log.info("Found {} immediate rules to execute for course: {}", immediateRules.size(), courseId);

            int totalEnrolled = 0;
            int totalSkipped = 0;
            int rulesExecuted = 0;
            int rulesFailed = 0;

            for (CourseAssignmentRule rule : immediateRules) {
                try {
                    log.info("Executing immediate rule: '{}' (ID: {})", rule.getRuleName(), rule.getRuleId());

                    RuleExecutionResultDto result = ruleExecutionService.executeRule(rule);

                    if (result.isSuccess()) {
                        totalEnrolled += result.getEnrollmentsCreated();
                        totalSkipped += result.getEnrollmentsSkipped();
                        rulesExecuted++;
                        log.info("Rule '{}' executed successfully: {} enrollments created, {} skipped",
                                rule.getRuleName(), result.getEnrollmentsCreated(), result.getEnrollmentsSkipped());
                    } else {
                        rulesFailed++;
                        log.error("Rule '{}' execution failed: {}", rule.getRuleName(), result.getMessage());
                    }

                } catch (Exception e) {
                    rulesFailed++;
                    log.error("Error executing immediate rule {} ({}): {}",
                            rule.getRuleName(), rule.getRuleId(), e.getMessage(), e);
                }
            }

            log.info("=== Immediate Rules Execution Summary ===");
            log.info("Total rules found: {}", immediateRules.size());
            log.info("Rules executed successfully: {}", rulesExecuted);
            log.info("Rules failed: {}", rulesFailed);
            log.info("Total enrollments created: {}", totalEnrolled);
            log.info("Total enrollments skipped: {}", totalSkipped);
            log.info("===========================================");

        } catch (Exception e) {
            log.error("Error executing assignment rules for course {}: {}", courseId, e.getMessage(), e);
            // Don't fail course publish if rule execution fails
        }
    }

    @Override
    public CourseDto unpublishCourse(Long courseId) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        c.setCourseStatus(CourseStatus.DRAFT);
        c.setPublishedAt(null);
        Course saved = courseRepository.save(c);
        return mapToDto(saved);
    }

    @Override
    public void deleteCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found: " + courseId);
        }
        courseRepository.deleteById(courseId);
    }

    @Override
    public long countByStatus(CourseStatus status) {
        return courseRepository.countByCourseStatus(status);
    }

    // mapping helpers
    private CourseDto mapToDto(Course course) {
        CourseDto dto = modelMapper.map(course, CourseDto.class);
        // ModelMapper may already map nested lists; if not, map manually
        return dto;
    }

    private Course mapToEntity(CourseDto dto) {
        return modelMapper.map(dto, Course.class);
    }

    @Override
    @Transactional
    public Map<String, Object> reExecuteImmediateRules(Long courseId) {
        log.info("Re-executing immediate rules for published course: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        if (course.getCourseStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Course must be published to execute immediate rules");
        }

        List<CourseAssignmentRule> immediateRules = assignmentRuleRepository
                .findByCourse_CourseId(courseId).stream()
                .filter(rule -> rule.isActive()
                        && rule.getRuleStatus() == RuleStatus.ACTIVE
                        && rule.getExecutionFrequency() == ExecutionFrequency.IMMEDIATE)
                .collect(Collectors.toList());

        if (immediateRules.isEmpty()) {
            return Map.of(
                    "success", false,
                    "message", "No immediate rules found for this course",
                    "rulesExecuted", 0,
                    "enrollmentsCreated", 0
            );
        }

        int totalEnrollments = 0;
        int rulesExecuted = 0;

        for (CourseAssignmentRule rule : immediateRules) {
            try {
                RuleExecutionResultDto result = ruleExecutionService.executeRule(rule);
                if (result.isSuccess()) {
                    totalEnrollments += result.getEnrollmentsCreated();
                    rulesExecuted++;
                }
            } catch (Exception e) {
                log.error("Error executing rule {}: {}", rule.getRuleId(), e.getMessage());
            }
        }

        return Map.of(
                "success", true,
                "message", String.format("Executed %d rule(s), created %d enrollment(s)", rulesExecuted, totalEnrollments),
                "rulesExecuted", rulesExecuted,
                "enrollmentsCreated", totalEnrollments
        );
    }
}
