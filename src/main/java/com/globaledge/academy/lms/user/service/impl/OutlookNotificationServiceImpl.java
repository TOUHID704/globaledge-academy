package com.globaledge.academy.lms.user.service.impl;

import com.globaledge.academy.lms.user.service.NotificationService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Outlook/Microsoft-based email notification service implementation.
 * This service is activated when app.email.provider=outlook in application.properties
 */
@Slf4j
@Service("outlookNotificationService")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.email.provider", havingValue = "outlook", matchIfMissing = true)
public class OutlookNotificationServiceImpl implements NotificationService {

    private final JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Async("taskExecutor")
    @Override
    public void sendWelcomeEmail(String email, String username, String temporaryPassword) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Welcome to Global Edge Academy LMS");

            String emailBody = buildWelcomeEmailBody(username, temporaryPassword);
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("[Outlook] Welcome email sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("[Outlook] Failed to send welcome email to {}: {}", email, e.getMessage(), e);
            logEmailToConsole("WELCOME EMAIL [Outlook]", email, username, temporaryPassword);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("taskExecutor")
    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Password Reset Request - Global Edge Academy LMS");

            String emailBody = buildPasswordResetEmailBody(resetUrl);
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("[Outlook] Password reset email sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("[Outlook] Failed to send password reset email to {}: {}", email, e.getMessage(), e);
            logPasswordResetToConsole(email, resetUrl);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Async("taskExecutor")
    @Override
    public void sendPasswordChangedConfirmation(String email) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(email);
            helper.setSubject("Password Changed Successfully - Global Edge Academy LMS");

            String emailBody = buildPasswordChangedEmailBody();
            helper.setText(emailBody, true);

            mailSender.send(message);
            log.info("[Outlook] Password changed confirmation sent successfully to {}", email);

        } catch (MessagingException e) {
            log.error("[Outlook] Failed to send password changed confirmation to {}: {}", email, e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildWelcomeEmailBody(String username, String temporaryPassword) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #0078D4; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .credentials { background-color: #fff; padding: 15px; border-left: 4px solid #0078D4; margin: 20px 0; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #0078D4; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
                    .warning { color: #d32f2f; font-weight: bold; }
                    .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to Global Edge Academy LMS</h1>
                    </div>
                    <div class="content">
                        <p>Hello <strong>%s</strong>,</p>
                        <p>Your account has been successfully created. Here are your login credentials:</p>
                        
                        <div class="credentials">
                            <p><strong>Username:</strong> %s</p>
                            <p><strong>Temporary Password:</strong> %s</p>
                        </div>
                        
                        <p class="warning">⚠️ IMPORTANT: You must change your password on first login for security reasons.</p>
                        
                        <p>Click the button below to access the login page:</p>
                        <a href="%s/login" class="button">Login Now</a>
                        
                        <p style="margin-top: 20px;">If you have any questions or need assistance, please contact our support team.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Global Edge Academy. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, username, temporaryPassword, frontendUrl);
    }

    private String buildPasswordResetEmailBody(String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #0078D4; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #0078D4; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
                    .warning { color: #d32f2f; font-weight: bold; }
                    .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <p>Hello,</p>
                        <p>We received a request to reset your password for your Global Edge Academy LMS account.</p>
                        
                        <p>Click the button below to reset your password:</p>
                        <a href="%s" class="button">Reset Password</a>
                        
                        <p class="warning">⚠️ This link will expire in 1 hour.</p>
                        
                        <p>If you did not request a password reset, please ignore this email and your password will remain unchanged.</p>
                        
                        <p style="margin-top: 20px; font-size: 12px; color: #666;">
                            If the button doesn't work, copy and paste this link into your browser:<br>
                            <a href="%s">%s</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Global Edge Academy. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetUrl, resetUrl, resetUrl);
    }

    private String buildPasswordChangedEmailBody() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #0078D4; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Changed Successfully</h1>
                    </div>
                    <div class="content">
                        <p>Hello,</p>
                        <p>This is to confirm that your password has been successfully changed.</p>
                        
                        <p>If you did not make this change, please contact our support team immediately.</p>
                        
                        <p>Thank you for using Global Edge Academy LMS.</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 Global Edge Academy. All rights reserved.</p>
                        <p>This is an automated message. Please do not reply to this email.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    private void logEmailToConsole(String type, String email, String username, String temporaryPassword) {
        log.info("=".repeat(80));
        log.info("{}", type);
        log.info("To: {}", email);
        log.info("Username: {}", username);
        log.info("Temporary Password: {}", temporaryPassword);
        log.info("Login URL: {}/login", frontendUrl);
        log.info("IMPORTANT: Change password on first login");
        log.info("=".repeat(80));
    }

    private void logPasswordResetToConsole(String email, String resetUrl) {
        log.info("=".repeat(80));
        log.info("PASSWORD RESET EMAIL [Outlook]");
        log.info("To: {}", email);
        log.info("Reset URL: {}", resetUrl);
        log.info("=".repeat(80));
    }
}