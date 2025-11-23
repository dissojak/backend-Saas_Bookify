package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.BusinessImageResponse;
import com.bookify.backendbookify_saas.services.BusinessImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/owner/businesses")
@RequiredArgsConstructor
@Tag(name = "Owner - Business Images", description = "Manage business images")
public class BusinessImageController {

    private final BusinessImageService imageService;

    @PostMapping("/{businessId}/images")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Upload business image", description = "Add new image to business")
    public ResponseEntity<BusinessImageResponse> uploadImage(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody Map<String, String> request
    ) {
        Long ownerId = Long.parseLong(authentication.getName());
        String imageUrl = request.get("imageUrl");

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl is required");
        }

        BusinessImageResponse response = imageService.uploadImage(businessId, imageUrl, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{businessId}/images")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Get all business images", description = "Retrieve all images for a business")
    public ResponseEntity<List<BusinessImageResponse>> getImages(
            @PathVariable Long businessId
    ) {
        List<BusinessImageResponse> images = imageService.getBusinessImages(businessId);
        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{businessId}/images/{imageId}")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Delete business image", description = "Remove an image from business")
    public ResponseEntity<Void> deleteImage(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long imageId
    ) {
        Long ownerId = Long.parseLong(authentication.getName());
        imageService.deleteImage(imageId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{businessId}/images/reorder")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Reorder business images", description = "Change display order of images")
    public ResponseEntity<Void> reorderImages(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody Map<String, List<Long>> request
    ) {
        Long ownerId = Long.parseLong(authentication.getName());
        List<Long> imageIds = request.get("imageIds");

        if (imageIds == null || imageIds.isEmpty()) {
            throw new IllegalArgumentException("imageIds array is required");
        }

        imageService.reorderImages(businessId, imageIds, ownerId);
        return ResponseEntity.ok().build();
    }
}

