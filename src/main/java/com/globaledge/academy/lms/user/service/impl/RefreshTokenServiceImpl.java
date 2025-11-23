package com.globaledge.academy.lms.security.service.impl;

import com.globaledge.academy.lms.core.constants.SecurityConstants;
import com.globaledge.academy.lms.security.service.RefreshTokenService;
import com.globaledge.academy.lms.user.entity.RefreshToken;
import com.globaledge.academy.lms.user.entity.User;
import com.globaledge.academy.lms.user.exception.ExpiredRefreshTokenException;
import com.globaledge.academy.lms.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoke any existing non-revoked tokens for this user
        revokeAllUserTokens(user);

        // Use plusSeconds instead of plusMillis (LocalDateTime doesn’t support plusMillis)
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(LocalDateTime.now().plusSeconds(SecurityConstants.REFRESH_TOKEN_VALIDITY / 1000))
                .revoked(false)
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Created refresh token for user: {}", user.getUsername());

        return savedToken;
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ExpiredRefreshTokenException("Refresh token not found"));

        if (refreshToken.getRevoked()) {
            throw new ExpiredRefreshTokenException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            throw new ExpiredRefreshTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            log.info("Revoked refresh token for user: {}", refreshToken.getUser().getUsername());
        });
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
        log.info("Revoked all refresh tokens for user: {}", user.getUsername());
    }
}
