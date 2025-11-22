package com.globaledge.academy.lms.course.dto;

import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.CourseStatus;
import com.globaledge.academy.lms.course.enums.DifficultyLevel;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDto {

    private Long courseId;

    private String title;

    private String description;

    private Long thumbnailMediaId;

    private String thumbnailUrl;

    private CourseCategory courseCategory;

    private String instructor;

    private Integer estimatedDuration;

    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private CourseStatus courseStatus;

    private String createdBy;

    private List<CourseModuleDto> modules;

    private Integer immediateRulesExecuted;
    private Integer totalEnrollmentsCreated;
    private String publishSummary;
}
