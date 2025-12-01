package com.bookify.backendbookify_saas.service;

import com.bookify.backendbookify_saas.models.enums.SubscriptionPlan;

import java.math.BigDecimal;

/**
 * Service pour gérer les prix et durées des plans d'abonnement
 */
public interface SubscriptionPricingService {

    /**
     * Récupère le prix en millimes (pour Flouci) d'un plan
     */
    long getPriceInMillimes(SubscriptionPlan plan);

    /**
     * Récupère le prix en BigDecimal d'un plan
     */
    BigDecimal getPrice(SubscriptionPlan plan);

    /**
     * Récupère la durée en jours d'un plan
     */
    int getDurationInDays(SubscriptionPlan plan);
}
