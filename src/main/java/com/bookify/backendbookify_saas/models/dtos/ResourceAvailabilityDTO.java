package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for availability slot
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceAvailabilityDTO {
    private Long id;
    private Long resourceId;
    private String date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private Boolean isBooked;
}
