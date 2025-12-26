package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.ChangePasswordRequest;
import com.bookify.backendbookify_saas.models.dtos.ProfileImageResponse;
import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;
import com.bookify.backendbookify_saas.models.dtos.UserProfileUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    ProfileImageResponse uploadProfileImage(Long userId, MultipartFile file);
}
