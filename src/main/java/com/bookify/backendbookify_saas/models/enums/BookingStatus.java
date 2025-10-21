package com.bookify.backendbookify_saas.models.enums;

/**
 * Deprecated duplicate of BookingStatusEnum.
 * Use {@link BookingStatusEnum} instead.
 */
@Deprecated
public enum BookingStatus {
    PENDING,      // Waiting for confirmation
    CONFIRMED,    // Confirmed
    COMPLETED,    // Completed
    CANCELLED,    // Cancelled
    NO_SHOW       // Client did not show up
}
