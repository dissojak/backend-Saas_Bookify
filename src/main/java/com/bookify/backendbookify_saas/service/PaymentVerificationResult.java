package com.bookify.backendbookify_saas.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentVerificationResult {
    private String paymentId;
    private String status; // SUCCESS, PENDING, FAILED
    private Long amount;
    private boolean verified;
}

