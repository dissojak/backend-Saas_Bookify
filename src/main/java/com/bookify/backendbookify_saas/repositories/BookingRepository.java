package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for the base Booking entity.
 * Note: Specific queries should live in ServiceBookingRepository.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Intentionally minimal; specialized queries belong to ServiceBookingRepository.
}
