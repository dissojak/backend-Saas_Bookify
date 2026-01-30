package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a resource
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceCreateRequest {
    private String name;
    private String description;
    private String type;
    private Long templateId; // Optional - if provided, attributes will be pre-filled from template
    private List<ResourceAttributeDTO> attributes;
    private List<ResourcePricingOptionDTO> pricingOptions;
    private List<String> imageUrls;
    private List<Long> staffIds; // Staff members to assign to this resource
}
