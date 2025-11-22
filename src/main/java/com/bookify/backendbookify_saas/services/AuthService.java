package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.AuthResponse;
import com.bookify.backendbookify_saas.models.dtos.LoginRequest;
import com.bookify.backendbookify_saas.models.dtos.RefreshTokenResponse;
import com.bookify.backendbookify_saas.models.dtos.SignupRequest;
import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
    String activateAccount(String token);
    RefreshTokenResponse refreshToken(String refreshToken);
    String logout(String userId, String refreshToken);
    UserProfileResponse getCurrentUser(String userId);
    String generateRefreshTokenForUser(Long userId);
}
