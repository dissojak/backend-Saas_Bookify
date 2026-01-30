package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for resource template
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceTemplateResponse {
    private Long id;
    private String name;
    private String description;
    private Long businessId;
    private List<TemplateAttributeDTO> attributes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
