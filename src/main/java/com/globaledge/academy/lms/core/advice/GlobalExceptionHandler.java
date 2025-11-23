package com.globaledge.academy.lms.core.advice;

import com.globaledge.academy.lms.assignment.exception.AssignmentRuleNotFoundException;
import com.globaledge.academy.lms.assignment.exception.InvalidRuleCriteriaException;
import com.globaledge.academy.lms.assignment.exception.RuleExecutionException;
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
import org.springframework.security.authentication.CredentialsExpiredException;
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

        // Return specific error message from exception
        String message = ex.getMessage();
        String errorCode = "BAD_CREDENTIALS";

        // Determine specific error code based on message
        if (message.contains("username") || message.contains("email")) {
            errorCode = "INVALID_USERNAME";
        } else if (message.contains("password")) {
            errorCode = "INVALID_PASSWORD";
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiError.builder()
                        .message(message)
                        .errorCode(errorCode)
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

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ApiError> handleCredentialsExpired(CredentialsExpiredException ex) {
        log.error("Credentials expired: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiError.builder()
                        .message("Your password has expired. Please change your password to continue.")
                        .errorCode("PASSWORD_EXPIRED")
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);

        // If it's a wrapped authentication exception, extract the message
        if (ex.getMessage() != null && ex.getMessage().startsWith("Authentication failed:")) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiError.builder()
                            .message(ex.getMessage())
                            .errorCode("AUTHENTICATION_FAILED")
                            .build());
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("RUNTIME_ERROR")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        log.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .message("An unexpected error occurred. Please try again later.")
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .build());
    }

    // ========================================================================
    // ASSIGNMENT RULE EXCEPTIONS
    // ========================================================================

    @ExceptionHandler(AssignmentRuleNotFoundException.class)
    public ResponseEntity<ApiError> handleAssignmentRuleNotFound(AssignmentRuleNotFoundException ex) {
        log.error("Assignment rule not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("ASSIGNMENT_RULE_NOT_FOUND")
                        .build());
    }

    @ExceptionHandler(InvalidRuleCriteriaException.class)
    public ResponseEntity<ApiError> handleInvalidRuleCriteria(InvalidRuleCriteriaException ex) {
        log.error("Invalid rule criteria: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("INVALID_RULE_CRITERIA")
                        .build());
    }

    @ExceptionHandler(RuleExecutionException.class)
    public ResponseEntity<ApiError> handleRuleExecution(RuleExecutionException ex) {
        log.error("Rule execution error: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.builder()
                        .message(ex.getMessage())
                        .errorCode("RULE_EXECUTION_ERROR")
                        .build());
    }

}