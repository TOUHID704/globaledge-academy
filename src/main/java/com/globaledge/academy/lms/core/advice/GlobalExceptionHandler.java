package com.globaledge.academy.lms.core.advice;
import com.globaledge.academy.lms.core.dto.ApiError;
import com.globaledge.academy.lms.employee.exception.EmployeeImportProcessingException;
import com.globaledge.academy.lms.employee.exception.InvalidFileFormatException;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * Handles exceptions for invalid file formats (e.g., wrong extension, missing headers).
     * Returns HTTP 400 Bad Request.
     */
    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ApiError> handleInvalidFileFormat(InvalidFileFormatException ex) {
        log.error("Invalid file format: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder().message(ex.getMessage()).errorCode("INVALID_FILE_FORMAT").build());
    }

    /**
     * Handles exceptions when a requested resource (e.g., an import history record) is not found.
     * Returns HTTP 404 Not Found.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder().message(ex.getMessage()).errorCode("RESOURCE_NOT_FOUND").build());
    }

    /**
     * Handles exceptions that occur during the main file processing logic.
     * Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(EmployeeImportProcessingException.class)
    public ResponseEntity<ApiError> handleImportProcessing(EmployeeImportProcessingException ex) {
        log.error("Import processing error: {}", ex.getMessage(), ex.getCause());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder().message(ex.getMessage()).errorCode("IMPORT_PROCESSING_ERROR").build());
    }

    /**
     * Handles exceptions when an uploaded file exceeds the configured maximum size.
     * Returns HTTP 413 Payload Too Large.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.error("File size exceeded: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiError.builder().message("File size exceeds maximum allowed limit.").errorCode("FILE_TOO_LARGE").build());
    }

    /**
     * A catch-all handler for any other unexpected exceptions.
     * Returns HTTP 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder().message("An unexpected error occurred.").errorCode("INTERNAL_SERVER_ERROR").build());
    }
}