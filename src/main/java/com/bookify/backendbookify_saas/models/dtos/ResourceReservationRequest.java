package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a resource reservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceReservationRequest {
    private Long resourceId;
    private Long availabilityId;
    private Long pricingOptionId;
    private String notes;
}
