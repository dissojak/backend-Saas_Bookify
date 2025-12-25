package com.bookify.backendbookify_saas.models.enums;

/**
 * Possible statuses for staff availability
 */
public enum AvailabilityStatus {
    AVAILABLE,    // Staff is available for work
    FULL,         // Staff is working but all slots are booked (no free time >= min service duration)
    CLOSED,       // Business is closed (Sundays, weekend_day)
    SICK,         // Staff is sick
    VACATION,     // Staff is on vacation
    DAY_OFF,      // Staff has a day off
    UNAVAILABLE   // Staff is unavailable for other reasons
}

