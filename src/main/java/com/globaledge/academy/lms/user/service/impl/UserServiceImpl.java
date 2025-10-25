package com.globaledge.academy.lms.user.service.impl;


import com.globaledge.academy.lms.user.dto.SignupRequest;
import com.globaledge.academy.lms.user.dto.UserDto;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.enums.UserRole;
import com.globaledge.academy.lms.user.exception.UserAlreadyExistsException;
import com.globaledge.academy.lms.user.repository.UserRepository;
import com.globaledge.academy.lms.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto signup(SignupRequest signupRequest) {

        // Check if username or email already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Generate a random password if not provided
        String rawPassword = signupRequest.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            rawPassword = generateRandomPassword(8);
        }

        // Create User entity
        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(rawPassword))
                .userRole(UserRole.USER) // default role
                .build();

        User savedUser = userRepository.save(user);

        // TODO: send email to user with password (rawPassword)

        return UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .userRole(savedUser.getUserRole())
                .build();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<length; i++){
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
