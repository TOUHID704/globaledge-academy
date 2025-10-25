package com.globaledge.academy.lms.user.service;


import com.globaledge.academy.lms.user.dto.SignupRequest;
import com.globaledge.academy.lms.user.dto.UserDto;

public interface UserService {
    UserDto signup(SignupRequest signupRequest);
}
