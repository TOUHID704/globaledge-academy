package com.globaledge.academy.lms.user.dto;

import com.globaledge.academy.lms.user.enums.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private UserRole userRole;
}
