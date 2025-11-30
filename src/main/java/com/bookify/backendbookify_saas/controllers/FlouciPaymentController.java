package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.FlouciVerifyPaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.PaymentCreateRequest;
import com.bookify.backendbookify_saas.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments/flouci")
public class FlouciPaymentController {

    private final Logger log = LoggerFactory.getLogger(FlouciPaymentController.class);
    private final PaymentService paymentService;

    public FlouciPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Public: create a payment and log it
    @PostMapping("/generate")
    public ResponseEntity<FlouciGeneratePaymentResponse> generate(@RequestBody PaymentCreateRequest request) {
        log.info("Generate payment request for amount={} subscriptionId={} bookingId={}", request.getAmount(), request.getSubscriptionId());
        FlouciGeneratePaymentResponse resp = paymentService.createPaymentAndLog(request);
        return ResponseEntity.ok(resp);
    }

    // Public: verify payment (called by client after checkout) and process subscription or booking if provided
    @PostMapping("/verify")
    public ResponseEntity<FlouciVerifyPaymentResponse> verify(
            @RequestParam String paymentId,
            @RequestParam(required = false) Long subscriptionId,
            @RequestParam(required = false) Long bookingId
    ) {
        log.info("Verify payment {} subscriptionId={} bookingId={}", paymentId, subscriptionId, bookingId);
        FlouciVerifyPaymentResponse resp = paymentService.verifyPaymentAndProcess(paymentId, subscriptionId, bookingId);
        return ResponseEntity.ok(resp);
    }

    // Optional webhook endpoint for Flouci notifications (public)
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader(value = "X-Flouci-Signature", required = false) String signature) {
        // For now just log the payload; you can implement signature verification using Flouci secrets
        log.info("Flouci webhook received signature={}; payload={}", signature, payload);
        return ResponseEntity.ok("received");
    }
}
