package com.bookify.backendbookify_saas.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUpdateRequest {

    @Size(min = 2, max = 150, message = "Le nom doit contenir entre 2 et 150 caractères")
    private String name;           // optionnel

    @Size(min = 2, max = 200, message = "La localisation doit contenir entre 2 et 200 caractères")
    private String location;       // optionnel

    @Size(min = 8, max = 20, message = "Le numéro de téléphone doit contenir entre 8 et 20 caractères")
    private String phone;          // optionnel

    @Email(message = "L'email du business doit être valide")
    private String email;          // optionnel

    private Long categoryId;       // optionnel

    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;  // optionnel

    /**
     * Optional name of the weekend day for the business, e.g. "MONDAY".
     * If absent or null, no change is applied. If present and blank, will clear the weekend day.
     */
    private String weekendDay;
}
