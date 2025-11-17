package com.globaledge.academy.lms.course.controller;

import com.globaledge.academy.lms.course.dto.CourseDto;
import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.CourseStatus;
import com.globaledge.academy.lms.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @PostMapping("/createCourse")
    public ResponseEntity<CourseDto> createCourse(@RequestBody CourseDto courseDto){
        CourseDto createdCourse = courseService.createCourse(courseDto);
        return ResponseEntity.ok(createdCourse);
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDto> updateCourse(@PathVariable Long courseId, @RequestBody CourseDto courseDto) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, courseDto));
    }

    //  GET endpoints remain accessible to authenticated users
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

    @GetMapping("/published")
    public ResponseEntity<List<CourseDto>> getPublished() {
        return ResponseEntity.ok(courseService.getPublishedCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @PostMapping("/{courseId}/publish")
    public ResponseEntity<CourseDto> publishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.publishCourse(courseId));
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @PostMapping("/{courseId}/unpublish")
    public ResponseEntity<CourseDto> unpublishCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.unpublishCourse(courseId));
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')") //  Added
    @PostMapping("/{courseId}/execute-immediate-rules")
    public ResponseEntity<Map<String, Object>> executeImmediateRules(@PathVariable Long courseId) {
        CourseDto course = courseService.getCourseById(courseId);

        if (course.getCourseStatus() != CourseStatus.PUBLISHED) {
            throw new IllegalStateException("Course must be published before executing rules");
        }

        Map<String, Object> result = courseService.reExecuteImmediateRules(courseId);
        return ResponseEntity.ok(result);
    }
}