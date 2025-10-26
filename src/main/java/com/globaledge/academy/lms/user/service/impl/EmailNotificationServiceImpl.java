package com.globaledge.academy.lms.user.service.impl;

import com.globaledge.academy.lms.user.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailNotificationServiceImpl implements NotificationService {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Async("taskExecutor")
    @Override
    public void sendWelcomeEmail(String email, String username, String temporaryPassword) {
        // TODO: Integrate with actual email service (JavaMailSender, SendGrid, AWS SES, etc.)
        log.info("=".repeat(80));
        log.info("WELCOME EMAIL");
        log.info("To: {}", email);
        log.info("Subject: Welcome to Global Edge Academy LMS");
        log.info("Body:");
        log.info("Hello {},", username);
        log.info("");
        log.info("Your account has been created successfully.");
        log.info("Username: {}", username);
        log.info("Temporary Password: {}", temporaryPassword);
        log.info("");
        log.info("IMPORTANT: You must change your password on first login.");
        log.info("Login URL: {}/login", frontendUrl);
        log.info("=".repeat(80));

        // Actual implementation example:
        /*
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Welcome to Global Edge Academy LMS");
            helper.setText(buildWelcomeEmailBody(username, temporaryPassword), true);
            mailSender.send(message);
            log.info("Welcome email sent successfully to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", email, e.getMessage());
        }
        */
    }

    @Async("taskExecutor")
    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        log.info("=".repeat(80));
        log.info("PASSWORD RESET EMAIL");
        log.info("To: {}", email);
        log.info("Subject: Password Reset Request");
        log.info("Body:");
        log.info("You have requested to reset your password.");
        log.info("Click the link below to reset your password:");
        log.info("{}", resetUrl);
        log.info("This link will expire in 1 hour.");
        log.info("If you did not request this, please ignore this email.");
        log.info("=".repeat(80));
    }

    @Async("taskExecutor")
    @Override
    public void sendPasswordChangedConfirmation(String email) {
        log.info("=".repeat(80));
        log.info("PASSWORD CHANGED CONFIRMATION");
        log.info("To: {}", email);
        log.info("Subject: Password Changed Successfully");
        log.info("Body:");
        log.info("Your password has been changed successfully.");
        log.info("If you did not make this change, please contact support immediately.");
        log.info("=".repeat(80));
    }
}

