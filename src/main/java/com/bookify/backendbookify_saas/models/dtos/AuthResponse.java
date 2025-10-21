package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la réponse d'authentification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;
    private String refreshToken;
    private Long userId;
    private String name;
    private String email;
    private RoleEnum role;
    private String message;

    // New fields to indicate owner business state
    // ( usage only by owner )
    private Boolean hasBusiness;  // Use wrapper types so null means "not present" and Jackson will omit fields for non-owners
    private Long businessId;
    private String businessName;
}

