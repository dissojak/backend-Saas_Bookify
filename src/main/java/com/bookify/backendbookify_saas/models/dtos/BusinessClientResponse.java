package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO for BusinessClient response
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessClientResponse {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private String notes;
    private Long businessId;
    private String businessName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

