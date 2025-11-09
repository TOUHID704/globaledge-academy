package com.globaledge.academy.lms.course.dto;

import com.globaledge.academy.lms.course.entity.CourseModule;
import com.globaledge.academy.lms.course.enums.CourseCategory;
import com.globaledge.academy.lms.course.enums.DifficultyLevel;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto {

    private Long courseId;

    private String title;

    private String description;

    private String thumbnailUrl;

    private CourseCategory courseCategory;

    private boolean isPublished = false;

    private List<CourseModuleDto> modules;

}
