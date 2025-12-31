package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.RatingCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.RatingResponse;
import com.bookify.backendbookify_saas.services.impl.RatingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Rating operations.
 * Uses ServiceRating and BusinessRating entities for proper rating management.
 * Allows clients to create, update, and view ratings for completed bookings.
 */
@RestController
@RequestMapping("/v1/ratings")
@RequiredArgsConstructor
@Tag(name = "Ratings", description = "Endpoints for managing service and business ratings")
@Slf4j
public class RatingController {

    private final RatingServiceImpl ratingService;

    /**
     * Create or update ratings for a completed booking.
     * If ratings already exist, they will be updated.
     */
    @PostMapping
    @Operation(summary = "Create or update ratings", description = "Create or update service and business ratings for a completed booking")
    public ResponseEntity<?> createOrUpdateRating(
            Authentication authentication,
            @Valid @RequestBody RatingCreateRequest request
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            log.info("Received rating request: bookingId={}, serviceRating={}, businessRating={}", 
                    request.getBookingId(), request.getServiceRating(), request.getBusinessRating());
            
            Long userId = Long.parseLong(authentication.getName());
            RatingResponse response = ratingService.createOrUpdateRating(request, userId);
            log.info("Rating created/updated successfully for booking {}", request.getBookingId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.error("Rating creation/update failed - validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Rating creation/update failed - runtime error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Failed to process rating"));
        } catch (Exception e) {
            log.error("Rating creation/update failed - unexpected error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process rating"));
        }
    }

    /**
     * Get existing ratings for a specific booking.
     * Returns both service and business ratings if they exist.
     */
    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get ratings for booking", description = "Get existing service and business ratings for a specific booking")
    public ResponseEntity<?> getRatingForBooking(
            Authentication authentication,
            @PathVariable Long bookingId
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            Long userId = Long.parseLong(authentication.getName());
            RatingResponse response = ratingService.getRatingForBooking(bookingId, userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Error getting rating: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting rating: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get rating"));
        }
    }

    /**
     * Check if a booking has been rated.
     */
    @GetMapping("/booking/{bookingId}/exists")
    @Operation(summary = "Check if rating exists", description = "Check if ratings exist for a specific booking")
    public ResponseEntity<?> hasRating(
            Authentication authentication,
            @PathVariable Long bookingId
    ) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            Long userId = Long.parseLong(authentication.getName());
            boolean exists = ratingService.hasRating(bookingId, userId);
            return ResponseEntity.ok(Map.of("hasRating", exists));
        } catch (Exception e) {
            log.error("Error checking rating: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all ratings by the authenticated user.
     */
    @GetMapping("/my-ratings")
    @Operation(summary = "Get my ratings", description = "Get all ratings created by the authenticated user")
    public ResponseEntity<?> getMyRatings(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Authentication required"));
        }

        try {
            Long userId = Long.parseLong(authentication.getName());
            List<RatingResponse> ratings = ratingService.getRatingsByUser(userId);
            return ResponseEntity.ok(ratings);
        } catch (Exception e) {
            log.error("Error fetching ratings for user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get average rating for a service.
     */
    @GetMapping("/service/{serviceId}/average")
    @Operation(summary = "Get average service rating", description = "Get the average rating for a specific service")
    public ResponseEntity<?> getAverageServiceRating(@PathVariable Long serviceId) {
        try {
            Double avgRating = ratingService.getAverageServiceRating(serviceId);
            Long count = ratingService.getServiceRatingCount(serviceId);
            return ResponseEntity.ok(Map.of(
                    "serviceId", serviceId,
                    "averageRating", avgRating != null ? avgRating : 0,
                    "totalRatings", count
            ));
        } catch (Exception e) {
            log.error("Error getting service rating: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get average rating for a business.
     */
    @GetMapping("/business/{businessId}/average")
    @Operation(summary = "Get average business rating", description = "Get the average rating for a specific business")
    public ResponseEntity<?> getAverageBusinessRating(@PathVariable Long businessId) {
        try {
            Double avgRating = ratingService.getAverageBusinessRating(businessId);
            Long count = ratingService.getBusinessRatingCount(businessId);
            return ResponseEntity.ok(Map.of(
                    "businessId", businessId,
                    "averageRating", avgRating != null ? avgRating : 0,
                    "totalRatings", count
            ));
        } catch (Exception e) {
            log.error("Error getting business rating: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
