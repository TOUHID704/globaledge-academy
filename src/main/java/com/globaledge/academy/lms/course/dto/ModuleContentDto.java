package com.globaledge.academy.lms.course.dto;

import com.globaledge.academy.lms.course.entity.CourseModule;
import com.globaledge.academy.lms.course.enums.ContentType;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleContentDto {
    private Long moduleContentId;

    private String title;

    private Integer contentNumber;

    private ContentType contentType;

    private String mediaUrl;



}

