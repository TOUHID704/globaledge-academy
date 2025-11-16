package com.globaledge.academy.lms.course.controller;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.CourseStatus;
import com.globaledge.academy.lms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;


    @PostMapping("/createCourse")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto){
        CourseDto createdCourse = courseService.createCourse(courseDto);
        return ResponseEntity.ok(createdCourse);
    }

    // Update
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long courseId, @RequestBody CourseDto courseDto) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, courseDto));
    }

    // Get all, optional filters: ?status=PUBLISHED or ?category=TECHNICAL
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses(
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(required = false) CourseCategory category
    ) {
        if (status != null) {
            return ResponseEntity.ok(courseService.getCoursesByStatus(status));
        }
        if (category != null) {
            return ResponseEntity.ok(courseService.getCoursesByCategory(category));
        }
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // Get published only
    @GetMapping("/published")
    public ResponseEntity<List<CourseDto>> getPublished() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    // Get by id
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    // Publish
    @PostMapping("/{courseId}/publish")
    public ResponseEntity<CourseDto> publishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.publishCourse(courseId));
    }

    // Unpublish (move back to draft)
    @PostMapping("/{courseId}/unpublish")
    public ResponseEntity<CourseDto> unpublishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.unpublishCourse(courseId));
    }

    // Delete
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}
