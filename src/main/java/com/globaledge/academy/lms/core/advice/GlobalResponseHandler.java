package com.globaledge.academy.lms.core.advice;

import com.globaledge.academy.lms.core.dto.ApiError;
import com.globaledge.academy.lms.core.dto.ApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Intercepts successful responses from controllers to wrap them in a standard ApiResponse format.
 * This ensures all successful API calls have a consistent JSON structure.
 */
//@RestControllerAdvice(basePackages = "com.globaledgeacademy.userservice.controller")
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        // If the controller is returning an error, don't wrap it.
        if (body instanceof ApiError) {
            return body;
        }

        if(body instanceof String){
            return body;
        }

        // --- FIX STARTS HERE ---
        // If the body is a Resource (a file download), do not wrap it.
        // Let Spring's ResourceHttpMessageConverter handle it directly.
        if (body instanceof Resource) {
            return body;
        }
        // --- FIX ENDS HERE ---

        // If the controller already returned a pre-formatted ApiResponse, don't wrap it again.
        if (body instanceof ApiResponse) {
            return body;
        }

        // If the endpoint is for Swagger/OpenAPI documentation, don't wrap it.
        if (request.getURI().getPath().contains("api-docs")) {
            return body;
        }

        // For all other successful responses, wrap them in the standard ApiResponse structure.
        return ApiResponse.builder()
                .message("Operation completed successfully")
                .data(body)
                .build();
    }
}