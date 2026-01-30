package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for resource image
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceImageDTO {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isPrimary;
}
