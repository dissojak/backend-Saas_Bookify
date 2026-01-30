package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Request DTO for generating multiple availability slots in bulk
 * Example: create slots for next 30 days, 9am-6pm, 1-hour slots, excluding weekends
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAvailabilityBulkRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime dailyStartTime;
    private LocalTime dailyEndTime;
    private Integer slotDurationMinutes; // e.g., 60 for 1-hour slots
    private Boolean excludeWeekends;
    private List<LocalDate> excludedDates; // Specific dates to skip (holidays, etc.)
}
