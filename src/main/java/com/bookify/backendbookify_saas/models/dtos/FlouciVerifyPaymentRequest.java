package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlouciVerifyPaymentRequest {
    private String paymentId;
    private Long subscriptionId; // optional: if verifying subscription purchase
}

