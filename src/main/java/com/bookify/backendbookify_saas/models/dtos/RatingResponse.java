package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for rating response combining service and business ratings.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {

    // Booking context
    private Long bookingId;
    private Long serviceId;
    private String serviceName;
    private Long businessId;
    private String businessName;
    private String clientName;

    // Service rating details
    private Long serviceRatingId;
    private Integer serviceRating;
    private String serviceComment;
    private LocalDate serviceRatingDate;

    // Business rating details
    private Long businessRatingId;
    private Integer businessRating;
    private String businessComment;
    private LocalDate businessRatingDate;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Indicates if ratings already exist (for UI to show edit mode)
    private boolean hasExistingRating;
}
