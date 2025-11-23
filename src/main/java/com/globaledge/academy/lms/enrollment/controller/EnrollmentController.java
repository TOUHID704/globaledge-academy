// 🎯 enrollment/controller/EnrollmentController.java
package com.globaledge.academy.lms.enrollment.controller;

import com.globaledge.academy.lms.enrollment.dto.EnrollmentDto;
import com.globaledge.academy.lms.enrollment.dto.MyCoursesDto;
import com.globaledge.academy.lms.enrollment.service.EnrollmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Tag(name = "Enrollments", description = "Employee course enrollment management")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "Get My Enrolled Courses")
    @GetMapping("/my-courses")
    public ResponseEntity<MyCoursesDto> getMyEnrolledCourses(@RequestParam String employeeId) {
        return ResponseEntity.ok(enrollmentService.getMyEnrolledCourses(employeeId));
    }

    @Operation(summary = "Get Available Courses for Self-Enrollment")
    @GetMapping("/available-courses")
    public ResponseEntity<List<EnrollmentDto>> getAvailableCourses(@RequestParam String employeeId) {
        return ResponseEntity.ok(enrollmentService.getAvailableCourses(employeeId));
    }

    @Operation(summary = "Self-Enroll in a Course")
    @PostMapping("/self-enroll")
    public ResponseEntity<EnrollmentDto> selfEnroll(
            @RequestParam String employeeId,
            @RequestParam Long courseId) {
        return ResponseEntity.ok(enrollmentService.selfEnroll(employeeId, courseId));
    }

    @Operation(summary = "Update Course Progress")
    @PutMapping("/{enrollmentId}/progress")
    public ResponseEntity<EnrollmentDto> updateProgress(
            @PathVariable Long enrollmentId,
            @RequestParam Integer progressPercentage) {
        return ResponseEntity.ok(enrollmentService.updateProgress(enrollmentId, progressPercentage));
    }

    @Operation(summary = "Mark Course as Completed")
    @PostMapping("/{enrollmentId}/complete")
    public ResponseEntity<EnrollmentDto> markAsCompleted(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.markAsCompleted(enrollmentId));
    }
}