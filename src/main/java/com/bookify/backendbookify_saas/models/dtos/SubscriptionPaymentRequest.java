package com.bookify.backendbookify_saas.models.dtos;

import com.bookify.backendbookify_saas.models.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Requête pour initier un paiement de subscription
 * Le frontend envoie uniquement le plan, pas le montant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPaymentRequest {
    private SubscriptionPlan plan;
    private Long businessId; // L'entreprise qui souscrit
    private String successLink; // optionnel, sinon utilise le défaut
    private String failLink; // optionnel, sinon utilise le défaut
}
