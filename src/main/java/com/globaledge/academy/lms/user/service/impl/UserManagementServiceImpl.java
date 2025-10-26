package com.globaledge.academy.lms.user.service.impl;

import com.globaledge.academy.lms.core.util.PasswordGenerator;
import com.globaledge.academy.lms.user.dto.UserRegistrationRequest;
import com.globaledge.academy.lms.user.dto.UserResponse;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.enums.UserRole;
import com.globaledge.academy.lms.user.exception.UserAlreadyExistsException;
import com.globaledge.academy.lms.user.exception.UserNotFoundException;
import com.globaledge.academy.lms.user.repository.UserRepository;
import com.globaledge.academy.lms.user.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordGenerator passwordGenerator;

    @Override
    @Transactional
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Generate a random password if not provided
        String rawPassword = request.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = passwordGenerator.generateSecurePassword();
        }

        // Create User entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .userRole(UserRole.USER)
                .accountEnabled(true)
                .accountLocked(false)
                .passwordExpired(false) // Manual registration doesn't force password change
                .failedLoginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return mapToUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .userRole(user.getUserRole())
                .employeeId(user.getEmployeeId())
                .accountEnabled(user.getAccountEnabled())
                .accountLocked(user.getAccountLocked())
                .passwordExpired(user.getPasswordExpired())
                .createdAt(user.getCreatedAt())
                .build();
    }
}