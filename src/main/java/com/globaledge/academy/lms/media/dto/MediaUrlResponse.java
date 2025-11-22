// MediaUrlResponse.java
package com.globaledge.academy.lms.media.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaUrlResponse {
    private Long mediaId;
    private String presignedUrl;
    private LocalDateTime expiresAt;
    private String message;
}