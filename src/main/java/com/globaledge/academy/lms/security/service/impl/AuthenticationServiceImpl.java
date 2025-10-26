package com.globaledge.academy.lms.security.service.impl;

import com.globaledge.academy.lms.core.constants.SecurityConstants;
import com.globaledge.academy.lms.security.dto.LoginRequest;
import com.globaledge.academy.lms.security.dto.LoginResponse;
import com.globaledge.academy.lms.security.jwt.service.JwtService;
import com.globaledge.academy.lms.security.service.AuthenticationService;
import com.globaledge.academy.lms.security.service.RefreshTokenService;
import com.globaledge.academy.lms.user.entity.RefreshToken;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.repository.UserRepository;
import com.globaledge.academy.lms.user.service.impl.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            // Find user first to check account status
            User user = userRepository.findByUsernameOrEmail(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

            // Check if account is locked
            if (user.isAccountLockedTemporarily()) {
                throw new LockedException("Account is temporarily locked due to multiple failed login attempts");
            }

            // Check if account is enabled
            if (!user.getAccountEnabled()) {
                throw new LockedException("Account is disabled");
            }

            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Reset failed attempts on successful login
            user.resetFailedAttempts();
            userRepository.save(user);

            // Get the User entity from UserDetailsImpl
            User authenticatedUser = ((UserDetailsImpl) authentication.getPrincipal()).getUser();

            // Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(authenticatedUser);

            // Create refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authenticatedUser);

            log.info("User logged in successfully: {}", authenticatedUser.getUsername());

            return new LoginResponse(accessToken, refreshToken.getToken());

        } catch (BadCredentialsException e) {
            // Increment failed login attempts
            userRepository.findByUsernameOrEmail(request.getUsername()).ifPresent(user -> {
                user.incrementFailedAttempts();

                if (user.getFailedLoginAttempts() >= SecurityConstants.MAX_FAILED_ATTEMPTS) {
                    user.lockAccount((int) SecurityConstants.ACCOUNT_LOCK_DURATION_MINUTES);
                    log.warn("Account locked for user: {}", user.getUsername());
                }

                userRepository.save(user);
            });

            throw new BadCredentialsException("Invalid username or password");

        } catch (AuthenticationException ex) {
            log.error("Authentication failed: {}", ex.getMessage());
            throw new RuntimeException("Authentication failed: " + ex.getMessage());
        }
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String refreshTokenString) {
        // Verify refresh token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenString);

        User user = refreshToken.getUser();

        // Generate new access token
        String accessToken = jwtService.generateAccessToken(user);

        // Optionally rotate refresh token (create new one)
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        log.info("Token refreshed for user: {}", user.getUsername());

        return new LoginResponse(accessToken, newRefreshToken.getToken());
    }
}