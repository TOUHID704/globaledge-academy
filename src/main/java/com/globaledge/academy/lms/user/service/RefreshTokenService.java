package com.globaledge.academy.lms.security.service;

import com.globaledge.academy.lms.user.entity.RefreshToken;
import com.globaledge.academy.lms.user.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyRefreshToken(String token);
    void revokeRefreshToken(String token);
    void revokeAllUserTokens(User user);
}