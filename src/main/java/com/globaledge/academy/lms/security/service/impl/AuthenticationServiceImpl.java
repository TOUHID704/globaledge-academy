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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        try {
            // Step 1: Find user first to provide specific error messages
            User user = userRepository.findByUsernameOrEmail(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("Invalid username or email"));

            // Step 2: Check if account is locked
            if (user.isAccountLockedTemporarily()) {
                throw new LockedException("Your account is temporarily locked due to multiple failed login attempts. Please try again later.");
            }

            // Step 3: Check if account is enabled
            if (!user.getAccountEnabled()) {
                throw new LockedException("Your account is disabled. Please contact administrator.");
            }

            // Step 4: Verify password manually to give specific error
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                // Increment failed login attempts
                user.incrementFailedAttempts();

                if (user.getFailedLoginAttempts() >= SecurityConstants.MAX_FAILED_ATTEMPTS) {
                    user.lockAccount((int) SecurityConstants.ACCOUNT_LOCK_DURATION_MINUTES);
                    userRepository.save(user);
                    log.warn("Account locked for user: {}", user.getUsername());
                    throw new LockedException("Your account has been locked due to multiple failed login attempts. Please try again after 30 minutes.");
                }

                userRepository.save(user);
                log.warn("Failed login attempt {} for user: {}", user.getFailedLoginAttempts(), user.getUsername());
                throw new BadCredentialsException("Invalid password");
            }

            // Step 5: Password is correct - reset failed attempts
            user.resetFailedAttempts();
            userRepository.save(user);

            // Step 6: Generate JWT tokens
            String accessToken = jwtService.generateAccessToken(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            log.info("User logged in successfully: {}", user.getUsername());

            // Step 7: Check if password is expired and return appropriate response
            if (user.getPasswordExpired()) {
                log.info("User {} needs to change password (passwordExpired=true)", user.getUsername());
                return LoginResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken.getToken())
                        .passwordExpired(true)
                        .message("Your password has expired. Please change your password to continue.")
                        .build();
            }

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .passwordExpired(false)
                    .message("Login successful")
                    .build();

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", request.getUsername());
            throw e; // Re-throw with original message

        } catch (LockedException e) {
            log.error("Account locked for user: {}", request.getUsername());
            throw e; // Re-throw with original message

        } catch (CredentialsExpiredException e) {
            // This shouldn't happen now since we handle it manually above
            log.error("Credentials expired for user: {}", request.getUsername());
            throw new BadCredentialsException("Your password has expired. Please contact administrator.");

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new BadCredentialsException("Authentication failed: " + e.getMessage());

        } catch (Exception e) {
            log.error("Unexpected error during login for user {}: {}", request.getUsername(), e.getMessage(), e);
            throw new RuntimeException("An unexpected error occurred during login. Please try again.");
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

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .passwordExpired(user.getPasswordExpired())
                .message("Token refreshed successfully")
                .build();
    }
}