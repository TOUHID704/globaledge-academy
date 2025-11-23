package com.globaledge.academy.lms.security.jwt.service;

import com.globaledge.academy.lms.user.entity.User;

public interface JwtService {

    /**
     * Generate a JWT access token for the given user.
     * @param user the user entity
     * @return JWT access token string
     */
    String generateAccessToken(User user);

    /**
     * Generate a JWT refresh token for the given user.
     * @param user the user entity
     * @return JWT refresh token string
     */
    String generateRefreshToken(User user);

    /**
     * Extract the user ID from a JWT token.
     * @param token JWT token
     * @return user ID
     */
    Long getUserIdFromToken(String token);

    /**
     * Validate the JWT token for expiration and signature.
     * @param token JWT token
     * @return true if token is valid, false otherwise
     */
    boolean validateToken(String token);
}
