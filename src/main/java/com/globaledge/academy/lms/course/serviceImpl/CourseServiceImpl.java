package com.globaledge.academy.lms.course.serviceImpl;

import com.globaledge.academy.lms.course.dto.*;
import com.globaledge.academy.lms.course.entity.*;
import com.globaledge.academy.lms.course.enums.*;
import com.globaledge.academy.lms.course.repository.*;
import com.globaledge.academy.lms.course.service.CourseService;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

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
    public CourseDto publishCourse(Long courseId) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        c.setCourseStatus(CourseStatus.PUBLISHED);
        if (c.getPublishedAt() == null) c.setPublishedAt(LocalDateTime.now());
        Course saved = courseRepository.save(c);
        return mapToDto(saved);
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
}
