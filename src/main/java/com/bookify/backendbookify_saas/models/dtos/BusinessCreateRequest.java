package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCreateRequest {

    @NotBlank(message = "Le nom du business est obligatoire")
    @Size(min = 2, max = 150, message = "Le nom doit contenir entre 2 et 150 caractères")
    private String name;

    @NotBlank(message = "La localisation est obligatoire")
    @Size(min = 2, max = 200, message = "La localisation doit contenir entre 2 et 200 caractères")
    private String location;

    @Size(min = 8, max = 20, message = "Le numéro de téléphone doit contenir entre 8 et 20 caractères")
    private String phone;

    @Email(message = "L'email du business doit être valide")
    private String email;

    @NotNull(message = "La catégorie est obligatoire")
    private Long categoryId;
}

