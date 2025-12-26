package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for updating the authenticated user's profile.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 1, max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;

    @Email(message = "L'email doit être valide")
    private String email;

    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String phoneNumber;

    private String avatarUrl;
}
