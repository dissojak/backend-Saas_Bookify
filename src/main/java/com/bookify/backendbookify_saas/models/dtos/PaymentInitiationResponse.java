package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse lors de l'initiation d'un paiement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInitiationResponse {
    private String paymentId;
    private String checkoutUrl;
    private boolean success;
    private String errorMessage;
}

