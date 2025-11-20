package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating a service booking
 * Can reference either a User (clientId) or a BusinessClient (businessClientId)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBookingCreateRequest {

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    /**
     * Global user client ID (optional if businessClientId is provided)
     */
    private Long clientId;

    /**
     * Business-specific client ID (optional if clientId is provided)
     */
    private Long businessClientId;

    private Long staffId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    private String notes;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private BookingStatusEnum status;
}