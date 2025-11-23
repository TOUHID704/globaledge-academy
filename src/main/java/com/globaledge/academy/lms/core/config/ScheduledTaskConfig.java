package com.globaledge.academy.lms.core.config;

import com.globaledge.academy.lms.user.repository.PasswordResetTokenRepository;
import com.globaledge.academy.lms.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Configuration for scheduled tasks like token cleanup.
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTaskConfig {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    /**
     * Cleanup expired tokens every day at 2 AM.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting scheduled cleanup of expired tokens");

        LocalDateTime now = LocalDateTime.now();

        try {
            refreshTokenRepository.deleteExpiredTokens(now);
            log.info("Deleted expired refresh tokens");

            passwordResetTokenRepository.deleteExpiredTokens(now);
            log.info("Deleted expired password reset tokens");

            log.info("Token cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during token cleanup: {}", e.getMessage(), e);
        }
    }
}