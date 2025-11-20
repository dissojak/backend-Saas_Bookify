package com.bookify.backendbookify_saas.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing a service booking
 */
@Entity
@Table(name = "service_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBooking extends Booking {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_client_id")
    private BusinessClient businessClient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff staff;

    /**
     * Validate that at least one client type is set
     */
    public void validateClientPresence() {
        if (client == null && businessClient == null) {
            throw new IllegalStateException("Either client or businessClient must be set");
        }
        if (client != null && businessClient != null) {
            throw new IllegalStateException("Only one of client or businessClient can be set");
        }
    }
}
