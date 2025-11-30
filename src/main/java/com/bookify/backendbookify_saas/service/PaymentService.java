package com.bookify.backendbookify_saas.service;

import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.FlouciVerifyPaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.PaymentCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Payment;

public interface PaymentService {
    // Generate payment (call Flouci, persist a Payment log with PENDING)
    FlouciGeneratePaymentResponse createPaymentAndLog(PaymentCreateRequest request);

    // Verify payment with Flouci, update payment log and optional subscription or booking
    FlouciVerifyPaymentResponse verifyPaymentAndProcess(String paymentId, Long subscriptionId, Long bookingId);

    Payment findByTransactionRef(String transactionRef);
}
