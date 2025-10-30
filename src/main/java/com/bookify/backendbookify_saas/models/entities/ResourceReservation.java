package com.bookify.backendbookify_saas.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing a reservation of a resource by a client
 * Extends the base Booking class
 */
@Entity
@Table(name = "resource_reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceReservation extends Booking {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_availability_id", nullable = false)
    private ResourceAvailability resourceAvailability;

}