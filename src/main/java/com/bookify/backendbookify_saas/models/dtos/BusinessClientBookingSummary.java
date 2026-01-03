package com.bookify.backendbookify_saas.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for returning booking summary information about a business client
 * Used by the UI to determine what warning message to show before deletion
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BusinessClientBookingSummary {
    
    /**
     * Whether the client has any active (PENDING or CONFIRMED) bookings
     * If true, the client cannot be deleted
     */
    private boolean hasActiveBookings;
    
    /**
     * Whether the client has completed bookings
     * If true, show a specific warning about completed bookings being deleted
     */
    private boolean hasCompletedBookings;
    
    /**
     * Total count of all bookings for this client (excluding active ones)
     */
    private long count;
}
