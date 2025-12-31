package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for service booking response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceBookingResponse {

    private Long id;
    private Long serviceId;
    private String serviceName;
    private Integer serviceDuration; // Duration in minutes

    // Business information (derived from service)
    private Long businessId;
    private String businessName;

    // Client information (either User or BusinessClient)
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientType; // "USER" or "BUSINESS_CLIENT"

    private Long staffId;
    private String staffName;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private BookingStatusEnum status;
    private String notes;
    private BigDecimal price;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

