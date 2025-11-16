package com.globaledge.academy.lms.course.controller;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/addCourse")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto){
        CourseDto createdCourse = courseService.createCourse(courseDto);
        return ResponseEntity.ok(createdCourse);

    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(
            @PathVariable Long courseId,
            @RequestBody CourseDto courseDto
    ) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, courseDto));
    }

    @PostMapping("/{courseId}/publish")
    public ResponseEntity<CourseDto> publishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.publishCourse(courseId));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

}
