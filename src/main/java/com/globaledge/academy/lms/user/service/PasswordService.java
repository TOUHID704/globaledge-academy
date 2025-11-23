package com.globaledge.academy.lms.user.service;

public interface PasswordService {
    void changePassword(Long userId, String currentPassword, String newPassword);
    void initiatePasswordReset(String email);
    void resetPassword(String token, String newPassword);
}