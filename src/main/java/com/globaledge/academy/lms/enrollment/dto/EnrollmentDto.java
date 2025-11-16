// 🎯 enrollment/dto/EnrollmentDto.java
package com.globaledge.academy.lms.enrollment.dto;

import com.globaledge.academy.lms.enrollment.enums.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDto {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseThumbnail;
    private String courseInstructor;
    private Integer courseEstimatedDuration;
    private EnrollmentType enrollmentType;
    private EnrollmentStatus enrollmentStatus;
    private AssignmentType assignmentType;
    private LocalDate enrolledDate;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private Integer progressPercentage;
}