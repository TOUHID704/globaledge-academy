package com.globaledge.academy.lms.course.dto;

import com.globaledge.academy.lms.course.enums.ContentType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleContentDto {

    private Long moduleContentId;

    private String title;

    private Integer contentNumber;

    private ContentType contentType;

    private Long mediaId;

    private String mediaUrl;
}
