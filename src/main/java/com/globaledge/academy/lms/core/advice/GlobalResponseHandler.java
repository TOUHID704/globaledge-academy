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

@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // 1️⃣ Do not wrap Swagger/OpenAPI responses
        if (isSwaggerRequest(request)) {
            return body;
        }

        // 2️⃣ Do not wrap ApiError
        if (body instanceof ApiError) {
            return body;
        }

        // 3️⃣ Do not wrap strings (StringHttpMessageConverter)
        if (body instanceof String) {
            return body;
        }

        // 4️⃣ Do not wrap raw file resources (file downloads)
        if (body instanceof Resource) {
            return body;
        }

        // 5️⃣ If already wrapped, do not wrap again
        if (body instanceof ApiResponse) {
            return body;
        }

        // 6️⃣ Wrap all other successful responses
        return ApiResponse.builder()
                .message("Operation completed successfully")
                .data(body)
                .build();
    }

    /**
     * Checks whether the current request is for Swagger/OpenAPI documentation.
     * These requests must not be wrapped.
     */
    private boolean isSwaggerRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();

        return path.contains("swagger")
                || path.contains("api-docs")
                || path.contains("openapi")
                || path.contains("swagger-ui");
    }
}
