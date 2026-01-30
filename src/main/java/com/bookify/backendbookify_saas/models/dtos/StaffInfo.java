package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for staff member information in responses
 * Used by both Service and Resource features
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffInfo {
    private Long id;
    private String name;
    private String email;
    private String avatarUrl;
}
