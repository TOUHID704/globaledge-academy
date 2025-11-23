package com.globaledge.academy.lms.media.entity;

import com.globaledge.academy.lms.media.enums.MediaStatus;
import com.globaledge.academy.lms.media.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a media file stored in S3.
 * Tracks metadata about uploaded files.
 */
@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_s3_key", columnList = "s3_key"),
        @Index(name = "idx_media_type", columnList = "media_type"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false, unique = true, name = "s3_key")
    private String s3Key; // Unique key in S3 bucket

    @Column(nullable = false)
    private String s3Url; // Full S3 URL

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "media_type")
    private MediaType mediaType;

    @Column(nullable = false)
    private String contentType; // MIME type (e.g., image/jpeg, video/mp4)

    @Column(nullable = false)
    private Long fileSize; // Size in bytes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MediaStatus status = MediaStatus.ACTIVE;

    private String uploadedBy; // Username or employee ID

    @Column(length = 500)
    private String description;

    // Optional: Reference to related entity (course, module, etc.)
    private Long relatedEntityId;

    @Column(length = 50)
    private String relatedEntityType; // e.g., "COURSE", "MODULE_CONTENT"

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Additional metadata
    private Integer durationSeconds; // For videos

    private Integer width; // For images

    private Integer height; // For images
}