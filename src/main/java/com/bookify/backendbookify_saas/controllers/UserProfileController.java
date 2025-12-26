package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.ChangePasswordRequest;
import com.bookify.backendbookify_saas.models.dtos.ProfileImageResponse;
import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;
import com.bookify.backendbookify_saas.models.dtos.UserProfileUpdateRequest;
import com.bookify.backendbookify_saas.services.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints to manage the authenticated user's profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        Long userId = resolveUserId(authentication);
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    @PatchMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        Long userId = resolveUserId(authentication);
        return ResponseEntity.ok(userProfileService.updateProfile(userId, request));
    }

    @PostMapping("/me/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Change current user password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {
        Long userId = resolveUserId(authentication);
        userProfileService.changePassword(userId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/me/avatar", consumes = "multipart/form-data")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload or replace profile image")
    public ResponseEntity<ProfileImageResponse> uploadAvatar(
            Authentication authentication,
            @RequestPart("file") MultipartFile file) {
        Long userId = resolveUserId(authentication);
        ProfileImageResponse response = userProfileService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(response);
    }

    private Long resolveUserId(Authentication authentication) {
        Authentication auth = authentication;
        if (auth == null) {
            auth = SecurityContextHolder.getContext().getAuthentication();
        }
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return Long.parseLong(auth.getName());
    }
}
