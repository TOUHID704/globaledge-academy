package com.globaledge.academy.lms.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a standardized error response for the API.
 * This structure is used by the GlobalExceptionHandler.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    @Builder.Default
    private boolean success = false;

    private String message;

    private String errorCode;

    private List<String> details;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}