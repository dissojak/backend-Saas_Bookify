package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for changing the authenticated user's password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    @NotBlank(message = "Le mot de passe actuel est obligatoire")
    private String currentPassword;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire")
    @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères")
    private String newPassword;

    @NotBlank(message = "La confirmation du nouveau mot de passe est obligatoire")
    private String confirmNewPassword;
}
