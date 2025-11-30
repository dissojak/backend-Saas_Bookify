package com.bookify.backendbookify_saas.models.dtos;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PaymentCreateRequest {
    private BigDecimal amount;
    private String currency;
    private String successLink;
    private String failLink;
    private String developerTrackingId;
    private Long subscriptionId; // optional, if this payment is for a subscription
}
