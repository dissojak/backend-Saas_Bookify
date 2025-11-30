package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlouciVerifyPaymentResponse {
    private String paymentId;
    private String status;
    private Long amount;
    private String rawJson;
}

