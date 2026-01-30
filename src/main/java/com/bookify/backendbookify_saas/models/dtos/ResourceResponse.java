package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for resource with full details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private String status;
    private Long templateId;
    private String templateName;
    private List<ResourceAttributeDTO> attributes;
    private List<ResourcePricingOptionDTO> pricingOptions;
    private List<ResourceImageDTO> images;
    private List<StaffInfo> assignedStaff;
    private Long businessId;
    private String businessName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
