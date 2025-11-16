package com.globaledge.academy.lms.course.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseModuleDto {

    private Long courseModuleId;

    private String title;

    private Integer moduleNumber;

    private List<ModuleContentDto> moduleContents;
}
