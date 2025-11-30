package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlouciGeneratePaymentRequest {
    private Long amount;
    private String currency;
    private String acceptCard = "true";
    private Integer sessionTimeoutSecs = 1200;
    private String successLink;
    private String failLink;
    private String developerTrackingId;
}

