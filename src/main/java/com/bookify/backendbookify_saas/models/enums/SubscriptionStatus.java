// src/main/java/com/bookify/backendbookify_saas/models/enums/SubscriptionStatus.java
package com.bookify.backendbookify_saas.models.enums;

/**
 * Statuts pour les abonnements.
 * Adjust values as needed for your business flow.
 */
public enum SubscriptionStatus {
    PENDING,   // created but not active yet (e.g. waiting payment)
    ACTIVE,    // subscription active
    FAILED,    // payment failed
    CANCELED,  // user cancelled
    EXPIRED    // ended by time
}