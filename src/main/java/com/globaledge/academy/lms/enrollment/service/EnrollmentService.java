// 🎯 enrollment/service/EnrollmentService.java
package com.globaledge.academy.lms.enrollment.service;

import com.globaledge.academy.lms.enrollment.dto.EnrollmentDto;
import com.globaledge.academy.lms.enrollment.dto.MyCoursesDto;
import com.globaledge.academy.lms.enrollment.enums.EnrollmentType;

import java.util.List;

public interface EnrollmentService {

    /**
     * Create enrollment from assignment rule (called by rule execution)
     * @return true if created, false if already enrolled
     */
    boolean createEnrollmentFromRule(Long employeeId, Long courseId,
                                     EnrollmentType enrollmentType, Integer dueDays);

    /**
     * Self-enrollment (user clicks "Enroll" button)
     */
    EnrollmentDto selfEnroll(String employeeId, Long courseId);

    /**
     * Get employee's enrolled courses with statistics
     */
    MyCoursesDto getMyEnrolledCourses(String employeeId);

    /**
     * Get available courses for self-enrollment (published courses not enrolled)
     */
    List<EnrollmentDto> getAvailableCourses(String employeeId);

    /**
     * Update progress percentage (called when user completes module/content)
     */
    EnrollmentDto updateProgress(Long enrollmentId, Integer progressPercentage);

    /**
     * Mark enrollment as completed
     */
    EnrollmentDto markAsCompleted(Long enrollmentId);
}