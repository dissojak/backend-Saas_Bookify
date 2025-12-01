package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.PaymentInitiationResponse;
import com.bookify.backendbookify_saas.models.dtos.PaymentVerificationResponse;
import com.bookify.backendbookify_saas.models.dtos.SubscriptionPaymentRequest;
import com.bookify.backendbookify_saas.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour gérer les paiements de subscriptions
 * Suit les principes SOLID - délègue la logique métier au PaymentService
 */
@RestController
@RequestMapping("/v1/subscriptions/payments")
@Tag(name = "Subscription Payments", description = "Endpoints pour gérer les paiements de subscriptions")
public class SubscriptionPaymentController {

    private final Logger log = LoggerFactory.getLogger(SubscriptionPaymentController.class);
    private final PaymentService paymentService;

    public SubscriptionPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Initie un paiement pour une subscription
     * Le frontend envoie uniquement le plan choisi, le montant est calculé côté backend
     */
    @PostMapping("/generate")
    @Operation(summary = "Initier un paiement de subscription",
               description = "Crée une subscription en statut PENDING et génère le lien de paiement")
    public ResponseEntity<PaymentInitiationResponse> initiatePayment(@RequestBody SubscriptionPaymentRequest request) {
        log.info("Initiating subscription payment: businessId={}, plan={}",
                request.getBusinessId(), request.getPlan());

        PaymentInitiationResponse response = paymentService.initiateSubscriptionPayment(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Vérifie un paiement après que l'utilisateur revienne de la page de paiement Flouci
     * Active la subscription si le paiement est réussi et crée un log de paiement
     */
    @GetMapping("/verify")
    @Operation(summary = "Vérifier un paiement",
               description = "Vérifie le statut du paiement et active la subscription si réussi")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestParam("payment_id") String paymentId,
            @RequestParam("subscriptionId") Long subscriptionId
    ) {
        log.info("Verifying payment: paymentId={}, subscriptionId={}", paymentId, subscriptionId);

        PaymentVerificationResponse response = paymentService.verifyAndCompleteSubscriptionPayment(
                paymentId, subscriptionId);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Webhook pour recevoir les notifications de Flouci (optionnel)
     */
    @PostMapping("/webhook")
    @Operation(summary = "Webhook Flouci", description = "Reçoit les notifications de paiement de Flouci")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Flouci-Signature", required = false) String signature
    ) {
        log.info("Flouci webhook received: signature={}, payload={}", signature, payload);
        // TODO: Implémenter la vérification de la signature et le traitement du webhook
        return ResponseEntity.ok("received");
    }
}

