package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.security.JwtService;
import com.bookify.backendbookify_saas.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Map;

/**
 * Controller for authentication (signup, login, refresh, logout, me)
 * Clean Architecture: Receives requests, validates data, calls service
 * Refresh tokens are stored in httpOnly secure cookies for security
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int COOKIE_MAX_AGE_7_DAYS = 7 * 24 * 60 * 60; // 7 days in seconds

    /**
     * Helper method to create httpOnly secure cookie for refresh token
     */
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Only sent over HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE_7_DAYS);
        cookie.setAttribute("SameSite", "Strict"); // CSRF protection
        response.addCookie(cookie);
    }

    /**
     * Helper method to extract refresh token from cookies
     */
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Helper method to clear refresh token cookie (for logout)
     */
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie immediately
        response.addCookie(cookie);
    }

    /**
     * Register a new user
     */
    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new user account")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.signup(request);

        // If user is verified (admin), set refresh token in httpOnly cookie
        if (authResponse.getToken() != null) {
            String refreshToken = authService.generateRefreshTokenForUser(authResponse.getUserId());
            setRefreshTokenCookie(response, refreshToken);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);

        // Set refresh token in httpOnly secure cookie
        String refreshToken = authService.generateRefreshTokenForUser(authResponse.getUserId());
        setRefreshTokenCookie(response, refreshToken);

        return ResponseEntity.ok(authResponse);
    }

    /**
     * Refresh JWT access token using refresh token from cookie
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT token", description = "Generate new access token using refresh token from cookie")
    public ResponseEntity<RefreshTokenResponse> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RefreshTokenResponse.builder()
                            .message("Refresh token not found")
                            .build());
        }

        RefreshTokenResponse refreshResponse = authService.refreshToken(refreshToken);

        // Generate and set new refresh token in cookie (token rotation)
        String userId = jwtService.extractUsername(refreshToken);
        String newRefreshToken = authService.generateRefreshTokenForUser(Long.parseLong(userId));
        setRefreshTokenCookie(response, newRefreshToken);

        return ResponseEntity.ok(refreshResponse);
    }

    /**
     * Logout user - clear refresh token cookie
     */
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Logout user", description = "Invalidate user session and clear refresh token")
    public ResponseEntity<Map<String, String>> logout(
            org.springframework.security.core.Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {

        // Ensure authentication is available (in case filter didn't set it)
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Unauthorized"));
        }

        String userId = authentication.getName(); // Get userId from JWT subject

        // Get refresh token from cookie for blacklisting (if implemented)
        String refreshToken = getRefreshTokenFromCookie(request);

        String message = authService.logout(userId, refreshToken);

        // Clear the refresh token cookie
        clearRefreshTokenCookie(response);

        return ResponseEntity.ok(Map.of("message", message));
    }

    /**
     * Get current authenticated user profile
     */
    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user", description = "Return authenticated user profile")
    public ResponseEntity<UserProfileResponse> getCurrentUser(org.springframework.security.core.Authentication authentication) {
        // Fallback to SecurityContext if parameter is null
        if (authentication == null) {
            authentication = SecurityContextHolder.getContext().getAuthentication();
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = authentication.getName(); // Returns userId from JWT subject
        UserProfileResponse profileResponse = authService.getCurrentUser(userId);
        return ResponseEntity.ok(profileResponse);
    }

    /**
     * Forgot password - Send 6-digit reset code via email
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Send 6-digit reset code to user's email")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        PasswordResetResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Reset password - Verify reset code and update password
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset password using 6-digit code from email")
    public ResponseEntity<PasswordResetResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        PasswordResetResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }
}
