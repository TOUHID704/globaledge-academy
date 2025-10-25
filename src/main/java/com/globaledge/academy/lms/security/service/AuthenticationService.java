package com.globaledge.academy.lms.security.service;


import com.globaledge.academy.lms.security.dto.LoginRequest;
import com.globaledge.academy.lms.security.dto.LoginResponse;

public interface AuthenticationService {

    /**
     * Authenticate user with username/email and password.
     * @param request login request
     * @return access & refresh token
     */
    LoginResponse login(LoginRequest request);

    /**
     * Refresh access token using refresh token.
     * @param refreshToken refresh token
     * @return new access token
     */
    LoginResponse refreshToken(String refreshToken);
}
