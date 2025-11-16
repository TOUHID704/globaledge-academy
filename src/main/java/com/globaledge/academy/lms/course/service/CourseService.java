package com.globaledge.academy.lms.course.service;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.enums.CourseCategory;
import java.util.List;

public interface CourseService {
    CourseDto createCourse(CourseDto courseDto);
    CourseDto updateCourse(Long courseId, CourseDto courseDto);
    CourseDto getCourseById(Long courseId);
    List<CourseDto> getAllCourses();
    List<CourseDto> getPublishedCourses();
    List<CourseDto> getCoursesByCategory(CourseCategory category);
    CourseDto publishCourse(Long courseId);
    CourseDto unpublishCourse(Long courseId);
    void deleteCourse(Long courseId);
}