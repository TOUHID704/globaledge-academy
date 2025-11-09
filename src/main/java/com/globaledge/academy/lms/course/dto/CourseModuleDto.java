package com.globaledge.academy.lms.course.dto;

import com.globaledge.academy.lms.course.entity.Course;
import com.globaledge.academy.lms.course.entity.ModuleContent;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CourseModuleDto {
    private Long courseModuleId;

    private String title;

    private Integer moduleNumber;

    private List<ModuleContentDto> moduleContents;

}
