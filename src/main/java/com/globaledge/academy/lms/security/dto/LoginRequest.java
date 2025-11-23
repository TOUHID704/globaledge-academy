package com.globaledge.academy.lms.security.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
