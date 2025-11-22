package com.globaledge.academy.lms.media.repository;

import com.globaledge.academy.lms.media.entity.Media;
import com.globaledge.academy.lms.media.enums.MediaStatus;
import com.globaledge.academy.lms.media.enums.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    /**
     * Find media by S3 key
     */
    Optional<Media> findByS3Key(String s3Key);

    /**
     * Find all media by type
     */
    List<Media> findByMediaType(MediaType mediaType);

    /**
     * Find all media by status
     */
    List<Media> findByStatus(MediaStatus status);

    /**
     * Find media by related entity
     */
    List<Media> findByRelatedEntityIdAndRelatedEntityType(Long entityId, String entityType);

    /**
     * Find media uploaded by user
     */
    List<Media> findByUploadedByOrderByUploadedAtDesc(String uploadedBy);

    /**
     * Find media by type and status
     */
    List<Media> findByMediaTypeAndStatus(MediaType mediaType, MediaStatus status);

    /**
     * Find orphaned media (no related entity, older than X days)
     */
    @Query("SELECT m FROM Media m WHERE m.relatedEntityId IS NULL " +
            "AND m.uploadedAt < :cutoffDate AND m.status = :status")
    List<Media> findOrphanedMedia(
            @Param("cutoffDate") LocalDateTime cutoffDate,
            @Param("status") MediaStatus status
    );

    /**
     * Count media by type
     */
    long countByMediaType(MediaType mediaType);

    /**
     * Get total storage used (in bytes)
     */
    @Query("SELECT SUM(m.fileSize) FROM Media m WHERE m.status = :status")
    Long getTotalStorageUsed(@Param("status") MediaStatus status);

    /**
     * Check if S3 key exists
     */
    boolean existsByS3Key(String s3Key);
}