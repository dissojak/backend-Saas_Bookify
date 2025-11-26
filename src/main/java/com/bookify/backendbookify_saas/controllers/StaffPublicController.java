package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;
import com.bookify.backendbookify_saas.services.StaffListingService;
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

    private final StaffListingService staffListingService;

    @GetMapping("/{businessId}/staffMembers")
    @Operation(summary = "List staff for business", description = "Public route that lists staff members for a business")
    public ResponseEntity<List<UserProfileResponse>> listBusinessStaff(@PathVariable Long businessId) {
        List<UserProfileResponse> dto = staffListingService.listStaffForBusiness(businessId);
        if (dto == null) dto = List.of();
        return ResponseEntity.ok(dto);
    }
}
