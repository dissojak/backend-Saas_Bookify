package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse après vérification d'un paiement de subscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResponse {
    private boolean success;
    private String status;
    private String message;
    private Long subscriptionId; // l'ID de la subscription créée/activée
}

