package com.bookify.backendbookify_saas.service;

import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentRequest;
import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.FlouciVerifyPaymentResponse;

public interface FlouciPaymentService {
    FlouciGeneratePaymentResponse generatePayment(FlouciGeneratePaymentRequest request);
    FlouciVerifyPaymentResponse verifyPayment(String paymentId);
}
