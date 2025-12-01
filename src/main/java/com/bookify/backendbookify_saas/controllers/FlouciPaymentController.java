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
 * Contrôleur pour les paiements Flouci (legacy - préférer SubscriptionPaymentController)
 * Conservé pour la compatibilité avec les anciennes intégrations
 */
@RestController
@RequestMapping("/v1/payments/flouci")
@Tag(name = "Flouci Payments", description = "Endpoints de paiement Flouci (legacy)")
@Deprecated
public class FlouciPaymentController {

    private final Logger log = LoggerFactory.getLogger(FlouciPaymentController.class);
    private final PaymentService paymentService;

    public FlouciPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Génère un lien de paiement pour une subscription
     * @deprecated Utiliser /v1/subscriptions/payments/generate à la place
     */
    @PostMapping("/generate")
    @Deprecated
    @Operation(summary = "Générer un paiement (legacy)", deprecated = true)
    public ResponseEntity<PaymentInitiationResponse> generate(@RequestBody SubscriptionPaymentRequest request) {
        log.info("Generate payment request for businessId={} plan={}", request.getBusinessId(), request.getPlan());
        PaymentInitiationResponse resp = paymentService.initiateSubscriptionPayment(request);
        return ResponseEntity.ok(resp);
    }

    /**
     * Vérifie un paiement après redirection depuis Flouci
     * @deprecated Utiliser /v1/subscriptions/payments/verify à la place
     */
    @PostMapping("/verify")
    @Deprecated
    @Operation(summary = "Vérifier un paiement (legacy)", deprecated = true)
    public ResponseEntity<PaymentVerificationResponse> verify(
            @RequestParam String paymentId,
            @RequestParam(required = false) Long subscriptionId
    ) {
        log.info("Verify payment {} subscriptionId={}", paymentId, subscriptionId);

        if (subscriptionId != null) {
            PaymentVerificationResponse resp = paymentService.verifyAndCompleteSubscriptionPayment(paymentId, subscriptionId);
            return ResponseEntity.ok(resp);
        }

        return ResponseEntity.badRequest().body(
                new PaymentVerificationResponse(false, "ERROR", "Subscription ID is required", null));
    }

    /**
     * Webhook optionnel pour les notifications Flouci
     */
    @PostMapping("/webhook")
    @Operation(summary = "Webhook Flouci")
    public ResponseEntity<String> webhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Flouci-Signature", required = false) String signature) {
        log.info("Flouci webhook received signature={}; payload={}", signature, payload);
        // TODO: Implémenter la vérification de signature et le traitement
        return ResponseEntity.ok("received");
    }
}

