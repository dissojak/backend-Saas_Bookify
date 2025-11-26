package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;
import com.bookify.backendbookify_saas.models.entities.Staff;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/businesses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public - Staff", description = "Public staff endpoints for businesses")
public class StaffPublicController {

    private final BusinessRepository businessRepository;
    private final StaffRepository staffRepository;

    @GetMapping("/{businessId}/staff")
    @Operation(summary = "List staff for business", description = "Public route that lists staff members for a business")
    public ResponseEntity<List<UserProfileResponse>> listBusinessStaff(@PathVariable Long businessId) {
        // Ensure business exists
        if (!businessRepository.existsById(businessId)) {
            throw new IllegalArgumentException("Business not found");
        }
        List<Staff> staffList = staffRepository.findByBusiness_Id(businessId);
        List<UserProfileResponse> dto = staffList.stream().map(this::toUserProfileDto).toList();
        return ResponseEntity.ok(dto);
    }

    private UserProfileResponse toUserProfileDto(Staff s) {
        return UserProfileResponse.builder()
                .userId(s.getId())
                .name(s.getName())
                .email(s.getEmail())
                .phoneNumber(s.getPhoneNumber())
                .role(s.getRole())
                .status(s.getStatus())
                .avatarUrl(s.getAvatarUrl())
                .build();
    }
}
