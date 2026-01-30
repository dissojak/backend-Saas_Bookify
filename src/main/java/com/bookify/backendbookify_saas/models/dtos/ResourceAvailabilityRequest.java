package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Request DTO for creating a single availability slot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAvailabilityRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
}
