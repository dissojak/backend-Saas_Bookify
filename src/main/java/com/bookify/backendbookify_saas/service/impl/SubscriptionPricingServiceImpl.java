package com.bookify.backendbookify_saas.service.impl;

import com.bookify.backendbookify_saas.models.enums.SubscriptionPlan;
import com.bookify.backendbookify_saas.service.SubscriptionPricingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SubscriptionPricingServiceImpl implements SubscriptionPricingService {

    @Override
    public long getPriceInMillimes(SubscriptionPlan plan) {
        // Prix en millimes (1 TND = 1000 millimes)
        switch (plan) {
            case BASIC:
                return 50_000; // 50 TND
            case PRO:
                return 125_000; // 125 TND
            case PREMIUM:
                return 500_000; // 500 TND
            default:
                return 0;
        }
    }

    @Override
    public BigDecimal getPrice(SubscriptionPlan plan) {
        return new BigDecimal(getPriceInMillimes(plan)).divide(new BigDecimal(1000));
    }

    @Override
    public int getDurationInDays(SubscriptionPlan plan) {
        switch (plan) {
            case BASIC:
                return 30;
            case PRO:
                return 90;
            case PREMIUM:
                return 365;
            default:
                return 0;
        }
    }
}

