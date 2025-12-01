package com.bookify.backendbookify_saas.service;

/**
 * Interface abstraite pour les passerelles de paiement
 * Permet d'ajouter facilement de nouveaux fournisseurs de paiement (SOLID - Open/Closed Principle)
 */
public interface PaymentGateway {

    /**
     * Génère un lien de paiement
     * @param amount montant en millimes
     * @param successLink lien de redirection en cas de succès
     * @param failLink lien de redirection en cas d'échec
     * @param developerTrackingId identifiant de suivi
     * @return réponse contenant le lien de paiement et l'ID de transaction
     */
    PaymentGatewayResponse generatePayment(long amount, String successLink, String failLink, String developerTrackingId);

    /**
     * Vérifie le statut d'un paiement
     * @param paymentId identifiant du paiement
     * @return statut du paiement
     */
    PaymentVerificationResult verifyPayment(String paymentId);

    /**
     * Nom du gateway (ex: "FLOUCI", "STRIPE", etc.)
     */
    String getGatewayName();
}
