package com.globaledge.academy.lms.user.service;

import com.globaledge.academy.lms.user.dto.UserRegistrationRequest;
import com.globaledge.academy.lms.user.dto.UserResponse;

public interface UserManagementService {
    UserResponse registerUser(UserRegistrationRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
}