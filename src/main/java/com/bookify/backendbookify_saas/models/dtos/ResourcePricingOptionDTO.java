package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.ResourcePricingTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for resource pricing option
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourcePricingOptionDTO {
    private Long id;
    private ResourcePricingTypeEnum pricingType;
    private BigDecimal price;
    private String label;
    private Integer minDuration;
    private Integer maxDuration;
    private Boolean isDefault;
}
