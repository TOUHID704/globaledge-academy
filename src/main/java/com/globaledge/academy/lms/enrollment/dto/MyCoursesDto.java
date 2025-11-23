// 🎯 enrollment/dto/MyCoursesDto.java
package com.globaledge.academy.lms.enrollment.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyCoursesDto {
    private List<EnrollmentDto> enrolledCourses;
    private Integer totalEnrolled;
    private Integer completedCount;
    private Integer inProgressCount;
    private Integer notStartedCount;
}