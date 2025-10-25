package com.globaledge.academy.lms.security.service.impl;

import com.globaledge.academy.lms.security.dto.LoginRequest;
import com.globaledge.academy.lms.security.dto.LoginResponse;
import com.globaledge.academy.lms.security.jwt.service.JwtService;
import com.globaledge.academy.lms.security.service.AuthenticationService;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.service.impl.UserDetailsImpl;
import com.globaledge.academy.lms.user.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Get the User entity from UserDetailsImpl
            User user = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            return new LoginResponse(accessToken, refreshToken);

        } catch (AuthenticationException ex) {
            throw new RuntimeException("Invalid username or password"); // You can create a custom exception
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        // Validate refresh token and issue new access token
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userDetailsService.getUserById(userId);

        String accessToken = jwtService.generateAccessToken(user);
        return new LoginResponse(accessToken, refreshToken);
    }
}
