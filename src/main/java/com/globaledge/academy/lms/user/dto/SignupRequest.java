package com.globaledge.academy.lms.user.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String email;
    private String password; // optional: can be generated automatically if left null
}
