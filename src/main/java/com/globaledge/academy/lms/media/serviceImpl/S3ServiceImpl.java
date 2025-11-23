package com.globaledge.academy.lms.media.serviceImpl;

import com.globaledge.academy.lms.media.exception.MediaUploadException;
import com.globaledge.academy.lms.media.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "true") // ✅ Only load when S3 enabled
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Override
    public String uploadFile(MultipartFile file, String s3Key) {
        try {
            log.info("Uploading file to S3: bucket={}, key={}", bucketName, s3Key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, s3Key);

            log.info("File uploaded successfully to S3: {}", s3Url);
            return s3Url;

        } catch (IOException e) {
            log.error("Failed to upload file to S3: {}", e.getMessage(), e);
            throw new MediaUploadException("Failed to upload file to S3: " + e.getMessage(), e);
        } catch (S3Exception e) {
            log.error("S3 service error during upload: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MediaUploadException("S3 error: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String s3Key, String contentType, long contentLength) {
        try {
            log.info("Uploading file to S3 from InputStream: bucket={}, key={}", bucketName, s3Key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(inputStream, contentLength));

            String s3Url = String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName, region, s3Key);

            log.info("File uploaded successfully to S3: {}", s3Url);
            return s3Url;

        } catch (S3Exception e) {
            log.error("S3 service error during upload: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MediaUploadException("S3 error: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public void deleteFile(String s3Key) {
        try {
            log.info("Deleting file from S3: bucket={}, key={}", bucketName, s3Key);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("File deleted successfully from S3: {}", s3Key);

        } catch (S3Exception e) {
            log.error("S3 service error during delete: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MediaUploadException("Failed to delete file from S3: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public String generatePresignedUrl(String s3Key, int expirationMinutes) {
        try {
            log.info("Generating pre-signed URL: bucket={}, key={}, expiration={}min",
                    bucketName, s3Key, expirationMinutes);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            log.info("Pre-signed URL generated successfully");
            return presignedUrl;

        } catch (S3Exception e) {
            log.error("S3 service error during presign: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MediaUploadException("Failed to generate pre-signed URL: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public boolean fileExists(String s3Key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;

        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("S3 service error during file existence check: {}", e.awsErrorDetails().errorMessage(), e);
            return false;
        }
    }

    @Override
    public FileMetadata getFileMetadata(String s3Key) {
        try {
            log.info("Getting file metadata: bucket={}, key={}", bucketName, s3Key);

            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            HeadObjectResponse response = s3Client.headObject(headObjectRequest);

            return new FileMetadata(
                    response.contentType(),
                    response.contentLength(),
                    response.eTag()
            );

        } catch (NoSuchKeyException e) {
            log.error("File not found in S3: {}", s3Key);
            throw new MediaUploadException("File not found: " + s3Key);
        } catch (S3Exception e) {
            log.error("S3 service error during metadata retrieval: {}", e.awsErrorDetails().errorMessage(), e);
            throw new MediaUploadException("Failed to get file metadata: " + e.awsErrorDetails().errorMessage(), e);
        }
    }
}