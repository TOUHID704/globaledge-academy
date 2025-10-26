package com.globaledge.academy.lms.core.advice;

import com.globaledge.academy.lms.core.dto.ApiError;
import com.globaledge.academy.lms.employee.exception.EmployeeImportProcessingException;
import com.globaledge.academy.lms.employee.exception.InvalidFileFormatException;
import com.globaledge.academy.lms.employee.exception.ResourceNotFoundException;
import com.globaledge.academy.lms.user.exception.ExpiredRefreshTokenException;
import com.globaledge.academy.lms.user.exception.InvalidPasswordResetTokenException;
import com.globaledge.academy.lms.user.exception.UserAlreadyExistsException;
import com.globaledge.academy.lms.user.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========================================================================
    // EMPLOYEE IMPORT EXCEPTIONS
    // ========================================================================

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ApiError> handleInvalidFileFormat(InvalidFileFormatException ex) {
        log.error("Invalid file format: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("INVALID_FILE_FORMAT")
                        .build());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("RESOURCE_NOT_FOUND")
                        .build());
    }

    @ExceptionHandler(EmployeeImportProcessingException.class)
    public ResponseEntity<ApiError> handleImportProcessing(EmployeeImportProcessingException ex) {
        log.error("Import processing error: {}", ex.getMessage(), ex.getCause());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("IMPORT_PROCESSING_ERROR")
                        .build());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.error("File size exceeded: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiError.builder()
                        .message("File size exceeds maximum allowed limit.")
                        .errorCode("FILE_TOO_LARGE")
                        .build());
    }

    // ========================================================================
    // USER MANAGEMENT EXCEPTIONS
    // ========================================================================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UserNotFoundException ex) {
        log.error("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("USER_NOT_FOUND")
                        .build());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("User already exists: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("USER_ALREADY_EXISTS")
                        .build());
    }

    // ========================================================================
    // AUTHENTICATION & SECURITY EXCEPTIONS
    // ========================================================================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        log.error("Bad credentials: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.builder()
                        .message("Invalid username or password")
                        .errorCode("BAD_CREDENTIALS")
                        .build());
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiError> handleAccountLocked(LockedException ex) {
        log.error("Account locked: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("ACCOUNT_LOCKED")
                        .build());
    }

    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ApiError> handleExpiredRefreshToken(ExpiredRefreshTokenException ex) {
        log.error("Expired refresh token: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("EXPIRED_REFRESH_TOKEN")
                        .build());
    }

    @ExceptionHandler(InvalidPasswordResetTokenException.class)
    public ResponseEntity<ApiError> handleInvalidPasswordResetToken(InvalidPasswordResetTokenException ex) {
        log.error("Invalid password reset token: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("INVALID_PASSWORD_RESET_TOKEN")
                        .build());
    }

    // ========================================================================
    // VALIDATION EXCEPTIONS
    // ========================================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("INVALID_ARGUMENT")
                        .build());
    }

    // ========================================================================
    // GENERIC EXCEPTION HANDLER
    // ========================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .message("An unexpected error occurred.")
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .build());
    }
}