package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for switching between BUSINESS_OWNER and STAFF context
 * Used when a BO who also works as staff wants to switch modes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwitchContextRequest {
    @NotNull(message = "activeMode is required")
    private String activeMode; // 'owner' or 'staff'
}
