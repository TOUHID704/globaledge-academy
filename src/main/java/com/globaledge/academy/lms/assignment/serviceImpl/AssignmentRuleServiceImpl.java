// 🎯 assignment/service/impl/AssignmentRuleServiceImpl.java
package com.globaledge.academy.lms.assignment.serviceImpl;

import com.globaledge.academy.lms.assignment.dto.*;
import com.globaledge.academy.lms.assignment.entity.*;
import com.globaledge.academy.lms.assignment.enums.*;
import com.globaledge.academy.lms.assignment.exception.*;
import com.globaledge.academy.lms.assignment.repository.*;
import com.globaledge.academy.lms.assignment.service.*;
import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.enums.CourseStatus;
import com.globaledge.academy.lms.course.repository.CourseRepository;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssignmentRuleServiceImpl implements AssignmentRuleService {

    private final AssignmentRuleRepository assignmentRuleRepository;
    private final CourseRepository courseRepository;
    private final RuleEvaluationService ruleEvaluationService;

    @Override
    @Transactional
    public AssignmentRuleDto createRule(AssignmentRuleDto ruleDto) {
        log.info("Creating assignment rule: {}", ruleDto.getRuleName());

        // Validate course exists and is published
        Course course = courseRepository.findById(ruleDto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + ruleDto.getCourseId()));

        if (course.getCourseStatus() != CourseStatus.PUBLISHED) {
            log.warn("Creating rule for unpublished course: {}. Rule will execute when course is published.",
                    course.getTitle());
        }

        // Validate criteria
        if (ruleDto.getCriteria() == null || ruleDto.getCriteria().isEmpty()) {
            throw new InvalidRuleCriteriaException("At least one criterion is required");
        }

        // Build rule entity
        CourseAssignmentRule rule = CourseAssignmentRule.builder()
                .ruleName(ruleDto.getRuleName())
                .description(ruleDto.getDescription())
                .course(course)
                .ruleType(ruleDto.getRuleType())
                .ruleStatus(RuleStatus.ACTIVE)
                .enrollmentType(ruleDto.getEnrollmentType())
                .dueDays(ruleDto.getDueDays())
                .executionFrequency(ruleDto.getExecutionFrequency())
                .matchLogic(ruleDto.getMatchLogic() != null ? ruleDto.getMatchLogic() : "AND")
                .createdBy(ruleDto.getCreatedBy())
                .isActive(true)
                .build();

        // Add criteria
        for (int i = 0; i < ruleDto.getCriteria().size(); i++) {
            RuleCriterionDto criterionDto = ruleDto.getCriteria().get(i);
            RuleCriterion criterion = RuleCriterion.builder()
                    .fieldName(criterionDto.getFieldName())
                    .operator(criterionDto.getOperator())
                    .fieldValue(criterionDto.getFieldValue())
                    .criterionOrder(i)
                    .build();
            rule.addCriterion(criterion);
        }

        CourseAssignmentRule savedRule = assignmentRuleRepository.save(rule);
        log.info("Assignment rule created successfully: {}", savedRule.getRuleId());

        return convertToDto(savedRule);
    }

    @Override
    @Transactional
    public AssignmentRuleDto updateRule(Long ruleId, AssignmentRuleDto ruleDto) {
        log.info("Updating assignment rule: {}", ruleId);

        CourseAssignmentRule rule = assignmentRuleRepository.findById(ruleId)
                .orElseThrow(() -> new AssignmentRuleNotFoundException("Rule not found with ID: " + ruleId));

        // Update basic fields
        rule.setRuleName(ruleDto.getRuleName());
        rule.setDescription(ruleDto.getDescription());
        rule.setRuleType(ruleDto.getRuleType());
        rule.setEnrollmentType(ruleDto.getEnrollmentType());
        rule.setDueDays(ruleDto.getDueDays());
        rule.setExecutionFrequency(ruleDto.getExecutionFrequency());
        rule.setMatchLogic(ruleDto.getMatchLogic() != null ? ruleDto.getMatchLogic() : "AND");

        // Update course if changed
        if (ruleDto.getCourseId() != null && !ruleDto.getCourseId().equals(rule.getCourse().getCourseId())) {
            Course course = courseRepository.findById(ruleDto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            rule.setCourse(course);
        }

        // Update criteria - clear and re-add
        rule.getCriteria().clear();
        if (ruleDto.getCriteria() != null) {
            for (int i = 0; i < ruleDto.getCriteria().size(); i++) {
                RuleCriterionDto criterionDto = ruleDto.getCriteria().get(i);
                RuleCriterion criterion = RuleCriterion.builder()
                        .fieldName(criterionDto.getFieldName())
                        .operator(criterionDto.getOperator())
                        .fieldValue(criterionDto.getFieldValue())
                        .criterionOrder(i)
                        .build();
                rule.addCriterion(criterion);
            }
        }

        CourseAssignmentRule updatedRule = assignmentRuleRepository.save(rule);
        log.info("Assignment rule updated successfully: {}", ruleId);

        return convertToDto(updatedRule);
    }

    @Override
    public AssignmentRuleDto getRuleById(Long ruleId) {
        CourseAssignmentRule rule = assignmentRuleRepository.findById(ruleId)
                .orElseThrow(() -> new AssignmentRuleNotFoundException("Rule not found with ID: " + ruleId));
        return convertToDto(rule);
    }

    @Override
    public List<AssignmentRuleDto> getAllRules() {
        return assignmentRuleRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentRuleDto> getActiveRules() {
        return assignmentRuleRepository.findByIsActiveTrueAndRuleStatus(RuleStatus.ACTIVE).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssignmentRuleDto> getRulesByCourse(Long courseId) {
        return assignmentRuleRepository.findByCourse_CourseId(courseId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        log.info("Deleting assignment rule: {}", ruleId);
        CourseAssignmentRule rule = assignmentRuleRepository.findById(ruleId)
                .orElseThrow(() -> new AssignmentRuleNotFoundException("Rule not found with ID: " + ruleId));
        assignmentRuleRepository.delete(rule);
        log.info("Assignment rule deleted successfully: {}", ruleId);
    }

    @Override
    @Transactional
    public AssignmentRuleDto activateRule(Long ruleId) {
        log.info("Activating assignment rule: {}", ruleId);
        CourseAssignmentRule rule = assignmentRuleRepository.findById(ruleId)
                .orElseThrow(() -> new AssignmentRuleNotFoundException("Rule not found with ID: " + ruleId));
        rule.setActive(true);
        rule.setRuleStatus(RuleStatus.ACTIVE);
        CourseAssignmentRule savedRule = assignmentRuleRepository.save(rule);
        return convertToDto(savedRule);
    }

    @Override
    @Transactional
    public AssignmentRuleDto deactivateRule(Long ruleId) {
        log.info("Deactivating assignment rule: {}", ruleId);
        CourseAssignmentRule rule = assignmentRuleRepository.findById(ruleId)
                .orElseThrow(() -> new AssignmentRuleNotFoundException("Rule not found with ID: " + ruleId));
        rule.setActive(false);
        rule.setRuleStatus(RuleStatus.INACTIVE);
        CourseAssignmentRule savedRule = assignmentRuleRepository.save(rule);
        return convertToDto(savedRule);
    }

    @Override
    public RulePreviewDto previewRule(AssignmentRuleDto ruleDto) {
        log.info("Previewing assignment rule: {}", ruleDto.getRuleName());

        // Validate course
        if (ruleDto.getCourseId() == null) {
            throw new InvalidRuleCriteriaException("Course ID is required for preview");
        }

        // Create temporary rule for evaluation
        CourseAssignmentRule tempRule = CourseAssignmentRule.builder()
                .matchLogic(ruleDto.getMatchLogic() != null ? ruleDto.getMatchLogic() : "AND")
                .build();

        // Add criteria
        if (ruleDto.getCriteria() != null) {
            for (RuleCriterionDto criterionDto : ruleDto.getCriteria()) {
                RuleCriterion criterion = RuleCriterion.builder()
                        .fieldName(criterionDto.getFieldName())
                        .operator(criterionDto.getOperator())
                        .fieldValue(criterionDto.getFieldValue())
                        .build();
                tempRule.addCriterion(criterion);
            }
        }

        // Evaluate rule
        return ruleEvaluationService.previewRule(tempRule, ruleDto.getCourseId());
    }

    private AssignmentRuleDto convertToDto(CourseAssignmentRule rule) {
        AssignmentRuleDto dto = AssignmentRuleDto.builder()
                .ruleId(rule.getRuleId())
                .ruleName(rule.getRuleName())
                .description(rule.getDescription())
                .courseId(rule.getCourse().getCourseId())
                .courseTitle(rule.getCourse().getTitle())
                .ruleType(rule.getRuleType())
                .ruleStatus(rule.getRuleStatus())
                .enrollmentType(rule.getEnrollmentType())
                .dueDays(rule.getDueDays())
                .executionFrequency(rule.getExecutionFrequency())
                .lastExecutedAt(rule.getLastExecutedAt())
                .lastMatchedCount(rule.getLastMatchedCount())
                .matchLogic(rule.getMatchLogic())
                .createdAt(rule.getCreatedAt())
                .createdBy(rule.getCreatedBy())
                .isActive(rule.isActive())
                .build();

        // Convert criteria
        if (rule.getCriteria() != null) {
            List<RuleCriterionDto> criteriaDto = rule.getCriteria().stream()
                    .map(criterion -> RuleCriterionDto.builder()
                            .criterionId(criterion.getCriterionId())
                            .fieldName(criterion.getFieldName())
                            .operator(criterion.getOperator())
                            .fieldValue(criterion.getFieldValue())
                            .build())
                    .collect(Collectors.toList());
            dto.setCriteria(criteriaDto);
        }

        return dto;
    }
}