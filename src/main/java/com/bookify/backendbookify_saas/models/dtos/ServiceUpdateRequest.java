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
public class ServiceUpdateRequest {
    private String name;
    private String description;
    private Integer durationMinutes;
    private BigDecimal price;
    private String imageUrl;
    private Boolean active;
    private List<Long> staffIds;
}

