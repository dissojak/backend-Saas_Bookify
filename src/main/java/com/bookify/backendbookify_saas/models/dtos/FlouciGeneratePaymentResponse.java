package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlouciGeneratePaymentResponse {
    private String paymentId;
    private String checkoutUrl;
    private String status;
    private String rawJson; // raw response for debug/log
}

