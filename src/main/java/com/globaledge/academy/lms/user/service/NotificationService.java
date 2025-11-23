package com.globaledge.academy.lms.user.service;

public interface NotificationService {
    void sendWelcomeEmail(String email, String username, String temporaryPassword);
    void sendPasswordResetEmail(String email, String resetToken);
    void sendPasswordChangedConfirmation(String email);
}
