package com.globaledge.academy.lms.media.controller;

import com.globaledge.academy.lms.media.dto.MediaDto;
import com.globaledge.academy.lms.media.dto.MediaUploadResponse;
import com.globaledge.academy.lms.media.dto.MediaUrlResponse;
import com.globaledge.academy.lms.media.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Media Controller - Only active when AWS S3 is enabled
 */
@RestController
@RequestMapping("/media")
@RequiredArgsConstructor
@Tag(name = "Media Management", description = "APIs for uploading and managing course media files")
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true") // ✅ Only load when S3 is enabled
public class MediaController {

    private final MediaService mediaService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload Media File",
            description = "Upload a file (image, video, or document) to S3 storage")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaUploadResponse> uploadMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam("mediaType") com.globaledge.academy.lms.media.enums.MediaType mediaType,
            @RequestParam("uploadedBy") String uploadedBy,
            @RequestParam(value = "description", required = false) String description) {

        MediaUploadResponse response = mediaService.uploadMedia(file, mediaType, uploadedBy, description);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Link Media to Entity",
            description = "Associate uploaded media with a course, module, or content")
    @PostMapping("/{mediaId}/link")
    public ResponseEntity<Map<String, String>> linkMediaToEntity(
            @PathVariable Long mediaId,
            @RequestParam Long entityId,
            @RequestParam String entityType) {

        mediaService.linkMediaToEntity(mediaId, entityId, entityType);
        return ResponseEntity.ok(Map.of(
                "message", "Media linked successfully",
                "mediaId", String.valueOf(mediaId),
                "entityType", entityType,
                "entityId", String.valueOf(entityId)
        ));
    }

    @Operation(summary = "Get Media by ID")
    @GetMapping("/{mediaId}")
    public ResponseEntity<MediaDto> getMediaById(@PathVariable Long mediaId) {
        return ResponseEntity.ok(mediaService.getMediaById(mediaId));
    }

    @Operation(summary = "Get Pre-signed URL",
            description = "Generate a temporary URL to access the media file")
    @GetMapping("/{mediaId}/url")
    public ResponseEntity<MediaUrlResponse> getPresignedUrl(
            @PathVariable Long mediaId,
            @RequestParam(defaultValue = "60") int expirationMinutes) {

        return ResponseEntity.ok(mediaService.getPresignedUrl(mediaId, expirationMinutes));
    }

    @Operation(summary = "Get Media by Type")
    @GetMapping("/type/{mediaType}")
    public ResponseEntity<List<MediaDto>> getMediaByType(@PathVariable com.globaledge.academy.lms.media.enums.MediaType mediaType) {
        return ResponseEntity.ok(mediaService.getMediaByType(mediaType));
    }

    @Operation(summary = "Get Media by Entity",
            description = "Get all media files associated with a specific entity")
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<MediaDto>> getMediaByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {

        return ResponseEntity.ok(mediaService.getMediaByEntity(entityId, entityType));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft Delete Media",
            description = "Mark media as deleted without removing from S3")
    @DeleteMapping("/{mediaId}")
    public ResponseEntity<Map<String, String>> deleteMedia(@PathVariable Long mediaId) {
        mediaService.deleteMedia(mediaId);
        return ResponseEntity.ok(Map.of("message", "Media soft deleted successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Permanently Delete Media",
            description = "Remove media from both S3 and database")
    @DeleteMapping("/{mediaId}/permanent")
    public ResponseEntity<Map<String, String>> permanentlyDeleteMedia(@PathVariable Long mediaId) {
        mediaService.permanentlyDeleteMedia(mediaId);
        return ResponseEntity.ok(Map.of("message", "Media permanently deleted"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cleanup Orphaned Media",
            description = "Delete media files not linked to any entity and older than specified days")
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOrphanedMedia(
            @RequestParam(defaultValue = "30") int daysOld) {

        int deletedCount = mediaService.cleanupOrphanedMedia(daysOld);
        return ResponseEntity.ok(Map.of(
                "message", "Cleanup completed",
                "deletedCount", deletedCount
        ));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get Storage Statistics")
    @GetMapping("/stats/storage")
    public ResponseEntity<Map<String, Object>> getStorageStats() {
        Long totalBytes = mediaService.getTotalStorageUsed();
        double totalMB = totalBytes != null ? totalBytes / (1024.0 * 1024.0) : 0;
        double totalGB = totalMB / 1024.0;

        return ResponseEntity.ok(Map.of(
                "totalBytes", totalBytes != null ? totalBytes : 0,
                "totalMB", String.format("%.2f", totalMB),
                "totalGB", String.format("%.2f", totalGB)
        ));
    }
}