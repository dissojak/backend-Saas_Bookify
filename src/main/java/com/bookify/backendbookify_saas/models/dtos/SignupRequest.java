package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;

/**
 * DTO pour la requête d'inscription d'un client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @Size(min = 8,max = 12, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String phoneNumber;

    private String avatarUrl;

    // Champ optionnel: si null, sera considéré comme CLIENT côté service
    private RoleEnum role;
}
