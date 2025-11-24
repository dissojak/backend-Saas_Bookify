package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateRequest {
    private String name;
    private String description;
    private Integer durationMinutes;
    private BigDecimal price;
    private String imageUrl;
    // staff IDs to assign to this service
    private List<Long> staffIds;
}

