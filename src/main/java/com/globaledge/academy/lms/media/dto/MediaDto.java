package com.globaledge.academy.lms.media.dto;

import com.globaledge.academy.lms.media.enums.MediaStatus;
import com.globaledge.academy.lms.media.enums.MediaType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDto {
    private Long mediaId;
    private String originalFileName;
    private String s3Key;
    private String s3Url;
    private MediaType mediaType;
    private String contentType;
    private Long fileSize;
    private MediaStatus status;
    private String uploadedBy;
    private String description;
    private Long relatedEntityId;
    private String relatedEntityType;
    private LocalDateTime uploadedAt;
    private LocalDateTime updatedAt;
    private Integer durationSeconds;
    private Integer width;
    private Integer height;
}