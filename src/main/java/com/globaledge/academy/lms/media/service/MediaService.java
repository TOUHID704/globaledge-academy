package com.globaledge.academy.lms.media.service;

import com.globaledge.academy.lms.media.dto.MediaDto;
import com.globaledge.academy.lms.media.dto.MediaUploadResponse;
import com.globaledge.academy.lms.media.dto.MediaUrlResponse;
import com.globaledge.academy.lms.media.enums.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for media management operations.
 */
public interface MediaService {

    /**
     * Upload media file
     * @param file File to upload
     * @param mediaType Type of media
     * @param uploadedBy User uploading the file
     * @param description Optional description
     * @return Upload response with media details
     */
    MediaUploadResponse uploadMedia(MultipartFile file, MediaType mediaType,
                                    String uploadedBy, String description);

    /**
     * Link uploaded media to an entity (course, module content, etc.)
     * @param mediaId Media ID
     * @param entityId Related entity ID
     * @param entityType Type of entity (e.g., "COURSE", "MODULE_CONTENT")
     */
    void linkMediaToEntity(Long mediaId, Long entityId, String entityType);

    /**
     * Get media by ID
     * @param mediaId Media ID
     * @return Media details
     */
    MediaDto getMediaById(Long mediaId);

    /**
     * Get pre-signed URL for media access
     * @param mediaId Media ID
     * @param expirationMinutes URL validity duration
     * @return Pre-signed URL response
     */
    MediaUrlResponse getPresignedUrl(Long mediaId, int expirationMinutes);

    /**
     * Get all media by type
     * @param mediaType Media type
     * @return List of media
     */
    List<MediaDto> getMediaByType(MediaType mediaType);

    /**
     * Get media by related entity
     * @param entityId Entity ID
     * @param entityType Entity type
     * @return List of media
     */
    List<MediaDto> getMediaByEntity(Long entityId, String entityType);

    /**
     * Delete media (soft delete - marks as DELETED)
     * @param mediaId Media ID
     */
    void deleteMedia(Long mediaId);

    /**
     * Permanently delete media from S3 and database
     * @param mediaId Media ID
     */
    void permanentlyDeleteMedia(Long mediaId);

    /**
     * Clean up orphaned media (no linked entity, older than X days)
     * @param daysOld Delete media older than this many days
     * @return Number of files deleted
     */
    int cleanupOrphanedMedia(int daysOld);

    /**
     * Get total storage used
     * @return Storage in bytes
     */
    Long getTotalStorageUsed();
}