package com.globaledge.academy.lms.media.serviceImpl;

import com.globaledge.academy.lms.media.dto.MediaDto;
import com.globaledge.academy.lms.media.dto.MediaUploadResponse;
import com.globaledge.academy.lms.media.dto.MediaUrlResponse;
import com.globaledge.academy.lms.media.entity.Media;
import com.globaledge.academy.lms.media.enums.MediaStatus;
import com.globaledge.academy.lms.media.enums.MediaType;
import com.globaledge.academy.lms.media.exception.InvalidMediaTypeException;
import com.globaledge.academy.lms.media.exception.MediaNotFoundException;
import com.globaledge.academy.lms.media.exception.MediaUploadException;
import com.globaledge.academy.lms.media.repository.MediaRepository;
import com.globaledge.academy.lms.media.service.MediaService;
import com.globaledge.academy.lms.media.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true") // ✅ Only load when S3 enabled
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final S3Service s3Service;
    private final Tika tika = new Tika();

    @Override
    @Transactional
    public MediaUploadResponse uploadMedia(MultipartFile file, MediaType mediaType,
                                           String uploadedBy, String description) {
        try {
            log.info("Starting media upload: filename={}, type={}, size={}",
                    file.getOriginalFilename(), mediaType, file.getSize());

            validateFile(file, mediaType);
            String s3Key = generateS3Key(file.getOriginalFilename(), mediaType);
            String contentType = detectContentType(file);
            String s3Url = s3Service.uploadFile(file, s3Key);

            Media media = Media.builder()
                    .originalFileName(file.getOriginalFilename())
                    .s3Key(s3Key)
                    .s3Url(s3Url)
                    .mediaType(mediaType)
                    .contentType(contentType)
                    .fileSize(file.getSize())
                    .status(MediaStatus.ACTIVE)
                    .uploadedBy(uploadedBy)
                    .description(description)
                    .build();

            Media savedMedia = mediaRepository.save(media);
            log.info("Media uploaded successfully: id={}, s3Key={}", savedMedia.getMediaId(), s3Key);

            String presignedUrl = s3Service.generatePresignedUrl(s3Key, 60);

            return MediaUploadResponse.builder()
                    .mediaId(savedMedia.getMediaId())
                    .originalFileName(savedMedia.getOriginalFileName())
                    .s3Key(savedMedia.getS3Key())
                    .s3Url(savedMedia.getS3Url())
                    .presignedUrl(presignedUrl)
                    .mediaType(savedMedia.getMediaType())
                    .contentType(savedMedia.getContentType())
                    .fileSize(savedMedia.getFileSize())
                    .status(savedMedia.getStatus())
                    .uploadedAt(savedMedia.getUploadedAt())
                    .message("File uploaded successfully")
                    .build();

        } catch (Exception e) {
            log.error("Failed to upload media: {}", e.getMessage(), e);
            throw new MediaUploadException("Failed to upload media: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void linkMediaToEntity(Long mediaId, Long entityId, String entityType) {
        log.info("Linking media {} to entity: type={}, id={}", mediaId, entityType, entityId);

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with ID: " + mediaId));

        media.setRelatedEntityId(entityId);
        media.setRelatedEntityType(entityType.toUpperCase());
        mediaRepository.save(media);

        log.info("Media linked successfully: mediaId={}, entityType={}, entityId={}",
                mediaId, entityType, entityId);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaDto getMediaById(Long mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with ID: " + mediaId));
        return mapToDto(media);
    }

    @Override
    @Transactional(readOnly = true)
    public MediaUrlResponse getPresignedUrl(Long mediaId, int expirationMinutes) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with ID: " + mediaId));

        if (media.getStatus() != MediaStatus.ACTIVE) {
            throw new MediaNotFoundException("Media is not active");
        }

        String presignedUrl = s3Service.generatePresignedUrl(media.getS3Key(), expirationMinutes);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);

        return MediaUrlResponse.builder()
                .mediaId(mediaId)
                .presignedUrl(presignedUrl)
                .expiresAt(expiresAt)
                .message("Pre-signed URL generated successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaDto> getMediaByType(MediaType mediaType) {
        return mediaRepository.findByMediaType(mediaType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MediaDto> getMediaByEntity(Long entityId, String entityType) {
        return mediaRepository.findByRelatedEntityIdAndRelatedEntityType(entityId, entityType.toUpperCase())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteMedia(Long mediaId) {
        log.info("Soft deleting media: {}", mediaId);

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with ID: " + mediaId));

        media.setStatus(MediaStatus.INACTIVE);
        mediaRepository.save(media);

        log.info("Media soft deleted successfully: {}", mediaId);
    }

    @Override
    @Transactional
    public void permanentlyDeleteMedia(Long mediaId) {
        log.info("Permanently deleting media: {}", mediaId);

        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new MediaNotFoundException("Media not found with ID: " + mediaId));

        try {
            s3Service.deleteFile(media.getS3Key());
        } catch (Exception e) {
            log.error("Failed to delete file from S3: {}", e.getMessage());
        }

        mediaRepository.delete(media);
        log.info("Media permanently deleted: {}", mediaId);
    }

    @Override
    @Transactional
    public int cleanupOrphanedMedia(int daysOld) {
        log.info("Starting cleanup of orphaned media older than {} days", daysOld);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Media> orphanedMedia = mediaRepository.findOrphanedMedia(cutoffDate, MediaStatus.ACTIVE);

        int deletedCount = 0;
        for (Media media : orphanedMedia) {
            try {
                permanentlyDeleteMedia(media.getMediaId());
                deletedCount++;
            } catch (Exception e) {
                log.error("Failed to delete orphaned media {}: {}", media.getMediaId(), e.getMessage());
            }
        }

        log.info("Cleanup completed. Deleted {} orphaned media files", deletedCount);
        return deletedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalStorageUsed() {
        return mediaRepository.getTotalStorageUsed(MediaStatus.ACTIVE);
    }

    // Helper methods
    private void validateFile(MultipartFile file, MediaType mediaType) {
        if (file.isEmpty()) {
            throw new MediaUploadException("File is empty");
        }

        long maxSize = getMaxFileSize(mediaType);
        if (file.getSize() > maxSize) {
            throw new MediaUploadException(
                    String.format("File size exceeds maximum allowed size of %d MB", maxSize / (1024 * 1024))
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType, mediaType)) {
            throw new InvalidMediaTypeException("Invalid file type for media type: " + mediaType);
        }
    }

    private long getMaxFileSize(MediaType mediaType) {
        return switch (mediaType) {
            case VIDEO -> 500L * 1024 * 1024;
            case IMAGE -> 10L * 1024 * 1024;
            case DOCUMENT -> 50L * 1024 * 1024;
            case AUDIO -> 100L * 1024 * 1024;
            default -> 100L * 1024 * 1024;
        };
    }

    private boolean isValidContentType(String contentType, MediaType mediaType) {
        return switch (mediaType) {
            case VIDEO -> contentType.startsWith("video/");
            case IMAGE -> contentType.startsWith("image/");
            case DOCUMENT -> contentType.equals("application/pdf") ||
                    contentType.equals("application/msword") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                    contentType.equals("application/vnd.ms-powerpoint") ||
                    contentType.equals("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            case AUDIO -> contentType.startsWith("audio/");
            default -> true;
        };
    }

    private String detectContentType(MultipartFile file) {
        try {
            return tika.detect(file.getInputStream());
        } catch (Exception e) {
            log.warn("Failed to detect content type, using default: {}", e.getMessage());
            return file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        }
    }

    private String generateS3Key(String originalFileName, MediaType mediaType) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFileName);
        String prefix = mediaType.name().toLowerCase();

        return String.format("%s/%s_%s%s", prefix, timestamp, uuid, extension);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    private MediaDto mapToDto(Media media) {
        return MediaDto.builder()
                .mediaId(media.getMediaId())
                .originalFileName(media.getOriginalFileName())
                .s3Key(media.getS3Key())
                .s3Url(media.getS3Url())
                .mediaType(media.getMediaType())
                .contentType(media.getContentType())
                .fileSize(media.getFileSize())
                .status(media.getStatus())
                .uploadedBy(media.getUploadedBy())
                .description(media.getDescription())
                .relatedEntityId(media.getRelatedEntityId())
                .relatedEntityType(media.getRelatedEntityType())
                .uploadedAt(media.getUploadedAt())
                .updatedAt(media.getUpdatedAt())
                .durationSeconds(media.getDurationSeconds())
                .width(media.getWidth())
                .height(media.getHeight())
                .build();
    }
}