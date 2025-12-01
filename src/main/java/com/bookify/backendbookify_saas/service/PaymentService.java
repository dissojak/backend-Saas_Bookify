package com.bookify.backendbookify_saas.service;

import com.bookify.backendbookify_saas.models.dtos.PaymentInitiationResponse;
import com.bookify.backendbookify_saas.models.dtos.PaymentVerificationResponse;
import com.bookify.backendbookify_saas.models.dtos.SubscriptionPaymentRequest;
import com.bookify.backendbookify_saas.models.entities.Payment;

/**
 * Service principal pour gérer les paiements
 * Utilise le principe de Dependency Inversion (SOLID) en dépendant de l'abstraction PaymentGateway
 */
public interface PaymentService {

    /**
     * Initie un paiement pour une subscription
     * Crée la subscription en statut PENDING et génère le lien de paiement
     * Le montant est calculé selon le plan choisi
     */
    PaymentInitiationResponse initiateSubscriptionPayment(SubscriptionPaymentRequest request);

    /**
     * Vérifie un paiement et active la subscription si le paiement est réussi
     * Crée un log de paiement après vérification
     */
    PaymentVerificationResponse verifyAndCompleteSubscriptionPayment(String paymentId, Long subscriptionId);

    /**
     * Recherche un paiement par référence de transaction
     */
    Payment findByTransactionRef(String transactionRef);
}
