package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.models.enums.UserStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token; // Access token only (refresh token sent via httpOnly cookie)
    private Long userId;
    private String name;
    private String email;
    private String phone; // Optional phone number for user convenience
    private RoleEnum role;
    private UserStatusEnum status;
    private String avatar;
    private String message;

    // New fields to indicate owner business state
    // ( usage only by owner )
    private Boolean hasBusiness;  // Use wrapper types so null means "not present" and Jackson will omit fields for non-owners
    private Long businessId;
    private String businessName;

    // New fields for BO acting as staff in their own business
    private Boolean isAlsoStaff;  // Whether this BO also has a staff record in their business
    private Long staffId;  // The staff ID for this BO (if isAlsoStaff = true)
}

