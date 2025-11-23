package com.globaledge.academy.lms.user.dto;

import com.globaledge.academy.lms.user.enums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private UserRole userRole;
    private String employeeId;
    private Boolean accountEnabled;
    private Boolean accountLocked;
    private Boolean passwordExpired;
    private LocalDateTime createdAt;
}

