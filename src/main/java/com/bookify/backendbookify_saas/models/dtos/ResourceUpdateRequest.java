package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for updating a resource
 * All fields are optional to allow partial updates
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceUpdateRequest {
    private String name;
    private String description;
    private String type;
    private String status;
    private List<ResourceAttributeDTO> attributes;
    private List<ResourcePricingOptionDTO> pricingOptions;
    private List<String> imageUrls;
    private List<Long> staffIds;
}
