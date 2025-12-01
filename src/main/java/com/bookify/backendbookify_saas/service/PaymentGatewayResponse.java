package com.bookify.backendbookify_saas.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayResponse {
    private String paymentId;
    private String checkoutUrl;
    private boolean success;
    private String errorMessage;
}

