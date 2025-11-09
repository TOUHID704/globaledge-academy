package com.globaledge.academy.lms.course.service;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.entity.Course;

public interface CourseService {

    CourseDto createCourse(CourseDto courseDto);

}
