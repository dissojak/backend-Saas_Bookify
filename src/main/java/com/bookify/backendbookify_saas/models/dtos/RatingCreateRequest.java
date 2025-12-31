package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating or updating ratings for a booking.
 * Handles both service rating and business rating.
 * At least one rating (service or business) must be provided.
 * Validation is performed at the service level.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingCreateRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Min(value = 1, message = "Service rating must be at least 1")
    @Max(value = 5, message = "Service rating must be at most 5")
    private Integer serviceRating; // Optional - validated in service

    @Min(value = 1, message = "Business rating must be at least 1")
    @Max(value = 5, message = "Business rating must be at most 5")
    private Integer businessRating; // Optional - validated in service

    @Size(max = 1000, message = "Service comment cannot exceed 1000 characters")
    private String serviceComment;

    @Size(max = 1000, message = "Business comment cannot exceed 1000 characters")
    private String businessComment;
}
