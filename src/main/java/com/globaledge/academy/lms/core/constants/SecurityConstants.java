package com.globaledge.academy.lms.core.constants;

/**
 * Central location for all security-related constants.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Prevent instantiation
    }

    // JWT Constants
    public static final String JWT_HEADER = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer ";

    // Token Expiration (in milliseconds)
    public static final long ACCESS_TOKEN_VALIDITY = 30 * 60 * 1000; // 30 minutes
    public static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days
    public static final long PASSWORD_RESET_TOKEN_VALIDITY = 60 * 60 * 1000; // 1 hour

    // Security Headers
    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // Account Lock Constants
    public static final int MAX_FAILED_ATTEMPTS = 5;
    public static final long ACCOUNT_LOCK_DURATION_MINUTES = 30;

    // Public Endpoints
    public static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/",
            "/health"
    };
}