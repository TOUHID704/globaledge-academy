package com.globaledge.academy.lms.course.serviceImpl;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.dto.CourseModuleDto;
import com.globaledge.academy.lms.course.dto.ModuleContentDto;
import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.entity.CourseModule;
import com.globaledge.academy.lms.course.entity.ModuleContent;
import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.repository.CourseRepository;
import com.globaledge.academy.lms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;


    @Override
    public CourseDto createCourse(CourseDto courseDto) {
        Course courseToSave = modelMapper.map(courseDto, Course.class);
        courseToSave.setPublished(false);
        courseToSave.setCreatedAt(LocalDateTime.now());

        // Handle modules if present
        if (courseDto.getModules() != null) {
            courseToSave.setModules(new ArrayList<>());
            for (CourseModuleDto moduleDto : courseDto.getModules()) {
                CourseModule module = modelMapper.map(moduleDto, CourseModule.class);
                courseToSave.addModule(module);

                // Handle module contents
                if (moduleDto.getModuleContents() != null) {
                    module.setModuleContents(new ArrayList<>());
                    for (ModuleContentDto contentDto : moduleDto.getModuleContents()) {
                        ModuleContent content = modelMapper.map(contentDto, ModuleContent.class);
                        module.addContent(content);
                    }
                }
            }
        }

        Course savedCourse = courseRepository.save(courseToSave);
        return modelMapper.map(savedCourse, CourseDto.class);
    }

// ADD other methods (publishCourse, getAllCourses, etc.)

    @Override
    public CourseDto updateCourse(Long courseId, CourseDto courseDto) {
        return null;
    }

    @Override
    public CourseDto getCourseById(Long courseId) {
        return null;
    }

    @Override
    public List<CourseDto> getAllCourses() {
        return List.of();
    }

    @Override
    public List<CourseDto> getPublishedCourses() {
        return List.of();
    }

    @Override
    public List<CourseDto> getCoursesByCategory(CourseCategory category) {
        return List.of();
    }

    @Override
    public CourseDto publishCourse(Long courseId) {
        return null;
    }

    @Override
    public CourseDto unpublishCourse(Long courseId) {
        return null;
    }

    @Override
    public void deleteCourse(Long courseId) {

    }
}
