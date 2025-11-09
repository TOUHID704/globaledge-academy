package com.globaledge.academy.lms.course.serviceImpl;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.repository.CourseRepository;
import com.globaledge.academy.lms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Override
    public CourseDto createCourse(CourseDto courseDto) {
      Course courseToSave = modelMapper.map(courseDto, Course.class);
      courseToSave.setPublished(false);
      Course savedCourse = courseRepository.save(courseToSave);
      return modelMapper.map(savedCourse, CourseDto.class);
    }
}
