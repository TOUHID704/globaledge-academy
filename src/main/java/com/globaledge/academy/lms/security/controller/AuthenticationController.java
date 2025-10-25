package com.globaledge.academy.lms.security.controller;

import com.globaledge.academy.lms.security.dto.LoginRequest;
import com.globaledge.academy.lms.security.dto.LoginResponse;
import com.globaledge.academy.lms.security.service.AuthenticationService;
import com.globaledge.academy.lms.user.dto.SignupRequest;
import com.globaledge.academy.lms.user.dto.UserDto;
import com.globaledge.academy.lms.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    // Manual signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody SignupRequest signupRequest) {
        UserDto userDTO = userService.signup(signupRequest);
        return ResponseEntity.ok(userDTO);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // Refresh token endpoint
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestParam String refreshToken) {
        LoginResponse response = authenticationService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }
}
