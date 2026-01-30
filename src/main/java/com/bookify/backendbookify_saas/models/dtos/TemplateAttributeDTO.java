package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.AttributeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for template attribute definition
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateAttributeDTO {
    private String attributeKey;
    private AttributeTypeEnum attributeType;
    private Boolean required;
    private Integer displayOrder;
}
