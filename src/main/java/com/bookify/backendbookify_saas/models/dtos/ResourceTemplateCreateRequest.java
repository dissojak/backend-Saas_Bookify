package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a resource template
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceTemplateCreateRequest {
    private String name;
    private String description;
    private List<TemplateAttributeDTO> attributes;
}
