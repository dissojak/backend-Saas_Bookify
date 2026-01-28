package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.*;
import java.util.Map;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    String activateAccount(String token);
    RefreshTokenResponse refreshToken(String refreshToken);
    String logout(String userId, String refreshToken);
    UserProfileResponse getCurrentUser(String userId);
    String generateRefreshTokenForUser(Long userId);
    PasswordResetResponse forgotPassword(ForgotPasswordRequest request);
    PasswordResetResponse resetPassword(ResetPasswordRequest request);
    Map<String, Object> switchContext(String userId, String activeMode);
}
