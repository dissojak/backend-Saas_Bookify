package com.bookify.backendbookify_saas.models.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Class representing a staff member.
 * Staff members are users with STAFF role.
 */
@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
public class Staff extends User {

    @Column(name = "default_start_time")
    private LocalTime defaultStartTime;

    @Column(name = "default_end_time")
    private LocalTime defaultEndTime;

    @Column(name = "start_working_at")
    private LocalDate startWorkingAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_staff_business"))
    private Business business;

    @ManyToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    private List<Service> services;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StaffAvailability> availabilities;

    @OneToMany(mappedBy = "staff", fetch = FetchType.LAZY)
    private List<ServiceBooking> bookings;
}