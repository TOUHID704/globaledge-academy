package com.globaledge.academy.lms.media.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * Service interface for AWS S3 operations.
 */
public interface S3Service {

    /**
     * Upload file to S3
     * @param file MultipartFile to upload
     * @param s3Key Unique key for the file in S3
     * @return S3 URL of uploaded file
     */
    String uploadFile(MultipartFile file, String s3Key);

    /**
     * Upload file from InputStream to S3
     * @param inputStream InputStream of file data
     * @param s3Key Unique key for the file in S3
     * @param contentType MIME type of the file
     * @param contentLength Size of the file in bytes
     * @return S3 URL of uploaded file
     */
    String uploadFile(InputStream inputStream, String s3Key, String contentType, long contentLength);

    /**
     * Delete file from S3
     * @param s3Key Key of the file to delete
     */
    void deleteFile(String s3Key);

    /**
     * Generate pre-signed URL for temporary access
     * @param s3Key Key of the file
     * @param expirationMinutes Duration in minutes for URL validity
     * @return Pre-signed URL
     */
    String generatePresignedUrl(String s3Key, int expirationMinutes);

    /**
     * Check if file exists in S3
     * @param s3Key Key of the file
     * @return true if file exists
     */
    boolean fileExists(String s3Key);

    /**
     * Get file metadata
     * @param s3Key Key of the file
     * @return File metadata (size, content type, etc.)
     */
    FileMetadata getFileMetadata(String s3Key);

    /**
     * Simple DTO for file metadata
     */
    record FileMetadata(String contentType, long contentLength, String etag) {}
}