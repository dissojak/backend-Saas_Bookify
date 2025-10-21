package com.bookify.backendbookify_saas.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Class representing a staff member who is also a client
 */
@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
public class Staff extends Client {

    // No need for ID as it's inherited from Client -> User hierarchy

    @Column(name = "default_start_time")
    private LocalTime defaultStartTime;

    @Column(name = "default_end_time")
    private LocalTime defaultEndTime;

    @Column(name = "start_working_at")
    private LocalDate startWorkingAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    private List<Service> services;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StaffAvailability> availabilities;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    private List<ServiceBooking> bookings;

    // Custom constructor that takes a Client to convert from Client to Staff
    public Staff(Client client) {
        // Copy all basic properties from client
        super.setId(client.getId());
        super.setEmail(client.getEmail());
        super.setPassword(client.getPassword());
        super.setName(client.getName());
        super.setPhoneNumber(client.getPhoneNumber());
        super.setStatus(client.getStatus());
        super.setAvatarUrl(client.getAvatarUrl());
        super.setCreatedAt(client.getCreatedAt());
        super.setUpdatedAt(client.getUpdatedAt());
        // Any other properties from Client/User that should be copied
    }
}