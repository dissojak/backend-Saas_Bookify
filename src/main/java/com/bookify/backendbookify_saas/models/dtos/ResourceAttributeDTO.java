package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.AttributeTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for resource attribute value
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceAttributeDTO {
    private String attributeKey;
    private String attributeValue;
    private AttributeTypeEnum attributeType;
    private Integer displayOrder;
}
