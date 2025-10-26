package com.globaledge.academy.lms.user.service.impl;

import com.globaledge.academy.lms.core.constants.SecurityConstants;
import com.globaledge.academy.lms.core.util.PasswordGenerator;
import com.globaledge.academy.lms.core.validation.PasswordStrengthValidator;
import com.globaledge.academy.lms.user.entity.PasswordResetToken;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.exception.InvalidPasswordResetTokenException;
import com.globaledge.academy.lms.user.exception.UserNotFoundException;
import com.globaledge.academy.lms.user.repository.PasswordResetTokenRepository;
import com.globaledge.academy.lms.user.repository.UserRepository;
import com.globaledge.academy.lms.user.service.NotificationService;
import com.globaledge.academy.lms.user.service.PasswordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordStrengthValidator passwordStrengthValidator;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password strength
        List<String> validationErrors = passwordStrengthValidator.validate(newPassword);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Password validation failed: " + String.join(", ", validationErrors));
        }

        // Check if new password is same as current
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpired(false);
        user.setLastPasswordChangeDate(LocalDateTime.now());
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getUsername());

        // Send confirmation email
        notificationService.sendPasswordChangedConfirmation(user.getEmail());
    }

    @Override
    @Transactional
    public void initiatePasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        // Invalidate any existing reset tokens for this user
        passwordResetTokenRepository.invalidateAllUserTokens(user);

        // Generate new reset token
        String token = UUID.randomUUID().toString();

        // FIX: replace plusMillis with plusSeconds
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(SecurityConstants.PASSWORD_RESET_TOKEN_VALIDITY / 1000))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset initiated for user: {}", user.getUsername());

        // Send reset email
        notificationService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Invalid password reset token"));

        if (!resetToken.isValid()) {
            throw new InvalidPasswordResetTokenException("Password reset token is expired or already used");
        }

        // Validate new password strength
        List<String> validationErrors = passwordStrengthValidator.validate(newPassword);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException("Password validation failed: " + String.join(", ", validationErrors));
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordExpired(false);
        user.setLastPasswordChangeDate(LocalDateTime.now());
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successfully for user: {}", user.getUsername());

        // Send confirmation email
        notificationService.sendPasswordChangedConfirmation(user.getEmail());
    }
}
