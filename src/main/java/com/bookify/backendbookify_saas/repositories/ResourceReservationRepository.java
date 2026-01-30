package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.ResourceReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceReservationRepository extends JpaRepository<ResourceReservation, Long> {

    /**
     * Find all reservations for a client
     */
    List<ResourceReservation> findByClientId(Long clientId);

    /**
     * Find reservations for a resource (via availability)
     */
    @Query("SELECT rr FROM ResourceReservation rr " +
           "WHERE rr.resourceAvailability.resource.id = :resourceId")
    List<ResourceReservation> findByResourceId(@Param("resourceId") Long resourceId);

    /**
     * Find all reservations for a business
     */
    @Query("SELECT rr FROM ResourceReservation rr " +
           "WHERE rr.resourceAvailability.resource.business.id = :businessId")
    List<ResourceReservation> findByBusinessId(@Param("businessId") Long businessId);

    /**
     * Check if there's an active reservation for an availability slot
     */
    @Query("SELECT COUNT(rr) > 0 FROM ResourceReservation rr " +
           "WHERE rr.resourceAvailability.id = :availabilityId AND rr.status NOT IN ('CANCELLED', 'NO_SHOW')")
    boolean existsActiveReservation(@Param("availabilityId") Long availabilityId);

    /**
     * Find active reservations for an availability
     */
    @Query("SELECT rr FROM ResourceReservation rr " +
           "WHERE rr.resourceAvailability.id = :availabilityId AND rr.status NOT IN ('CANCELLED', 'NO_SHOW')")
    List<ResourceReservation> findActiveByAvailabilityId(@Param("availabilityId") Long availabilityId);

    /**
     * Find reservation with full details
     */
    @Query("SELECT rr FROM ResourceReservation rr " +
           "LEFT JOIN FETCH rr.client " +
           "LEFT JOIN FETCH rr.resourceAvailability ra " +
           "LEFT JOIN FETCH ra.resource " +
           "WHERE rr.id = :id")
    Optional<ResourceReservation> findByIdWithDetails(@Param("id") Long id);
}
