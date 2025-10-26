package com.globaledge.academy.lms.security.controller;

import com.globaledge.academy.lms.security.dto.*;
import com.globaledge.academy.lms.security.service.AuthenticationService;
import com.globaledge.academy.lms.user.dto.UserRegistrationRequest;
import com.globaledge.academy.lms.user.dto.UserResponse;
import com.globaledge.academy.lms.user.service.PasswordService;
import com.globaledge.academy.lms.user.service.UserManagementService;
import com.globaledge.academy.lms.user.service.impl.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and password management APIs")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserManagementService userManagementService;
    private final PasswordService passwordService;

    @Operation(summary = "User Registration", description = "Register a new user manually")
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userManagementService.registerUser(request);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = "User Login", description = "Authenticate user and receive access & refresh tokens")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh Token", description = "Get new access token using refresh token")
    @PostMapping("/refresh-token")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody TokenRefreshRequest request) {
        LoginResponse response = authenticationService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(new TokenRefreshResponse(response.getAccessToken(), response.getRefreshToken()));
    }

    @Operation(summary = "Change Password", description = "Change password for authenticated user")
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestBody PasswordChangeRequest request,
            Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getUser().getId();

        passwordService.changePassword(userId, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok("Password changed successfully");
    }

    @Operation(summary = "Initiate Password Reset", description = "Request password reset link via email")
    @PostMapping("/forgot-password")
    public ResponseEntity<String> initiatePasswordReset(@RequestBody PasswordResetInitiationRequest request) {
        passwordService.initiatePasswordReset(request.getEmail());
        return ResponseEntity.ok("Password reset link has been sent to your email");
    }

    @Operation(summary = "Reset Password", description = "Reset password using reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest request) {
        passwordService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password has been reset successfully");
    }

    @Operation(summary = "Get Current User", description = "Get authenticated user information")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String username = userDetails.getUsername();
        UserResponse userResponse = userManagementService.getUserByUsername(username);
        return ResponseEntity.ok(userResponse);
    }
}
