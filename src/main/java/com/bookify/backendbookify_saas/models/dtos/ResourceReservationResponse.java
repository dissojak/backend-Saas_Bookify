package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Response DTO for resource reservation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceReservationResponse {
    private Long id;
    private Long resourceId;
    private String resourceName;
    private String resourcePrimaryImage;
    private Long clientId;
    private String clientName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal price;
    private String pricingType;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
