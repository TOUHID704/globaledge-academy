package com.globaledge.academy.lms.media.dto;

import com.globaledge.academy.lms.media.enums.MediaStatus;
import com.globaledge.academy.lms.media.enums.MediaType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUploadResponse {
    private Long mediaId;
    private String originalFileName;
    private String s3Key;
    private String s3Url;
    private String presignedUrl; // Temporary URL for access
    private MediaType mediaType;
    private String contentType;
    private Long fileSize;
    private MediaStatus status;
    private LocalDateTime uploadedAt;
    private String message;
}