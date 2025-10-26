package com.globaledge.academy.lms.security.dto;

import lombok.Data;

@Data
public class PasswordResetInitiationRequest {
    private String email;
}