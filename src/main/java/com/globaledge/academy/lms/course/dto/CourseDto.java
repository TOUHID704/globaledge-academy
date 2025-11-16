package com.globaledge.academy.lms.course.dto;

import com.globaledge.academy.lms.course.enums.CourseCategory;
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

    private String thumbnailUrl;

    private CourseCategory courseCategory;

    private boolean published;

    private String instructor;

    private Integer estimatedDuration;

    private LocalDateTime createdAt;

    private LocalDateTime publishedAt;

    private String createdBy;

    private List<CourseModuleDto> modules;   // Avoid exposing entities
}
