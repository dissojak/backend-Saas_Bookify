package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.ResourceAvailability;
import com.bookify.backendbookify_saas.models.enums.ResourceAvailabilityStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceAvailabilityRepository extends JpaRepository<ResourceAvailability, Long> {

    /**
     * Find all availabilities for a resource within a date range
     */
    List<ResourceAvailability> findByResourceIdAndDateBetween(Long resourceId, LocalDate from, LocalDate to);

    /**
     * Find all availabilities for a resource on a specific date
     */
    List<ResourceAvailability> findByResourceIdAndDate(Long resourceId, LocalDate date);

    /**
     * Find available slots for a specific date (status = AVAILABLE and not booked)
     */
    @Query("SELECT ra FROM ResourceAvailability ra " +
           "WHERE ra.resource.id = :resourceId AND ra.date = :date AND ra.status = 'AVAILABLE' " +
           "ORDER BY ra.startTime ASC")
    List<ResourceAvailability> findAvailableSlots(@Param("resourceId") Long resourceId, @Param("date") LocalDate date);

    /**
     * Find single availability slot
     */
    Optional<ResourceAvailability> findByIdAndResourceId(Long id, Long resourceId);

    /**
     * Delete all availabilities for a resource within a date range (for regenerating slots)
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ResourceAvailability ra WHERE ra.resource.id = :resourceId AND ra.date BETWEEN :startDate AND :endDate")
    void deleteByResourceIdAndDateBetween(@Param("resourceId") Long resourceId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Count availabilities with no reservations for a date range
     */
    @Query("SELECT COUNT(ra) FROM ResourceAvailability ra " +
           "WHERE ra.resource.id = :resourceId AND ra.date BETWEEN :startDate AND :endDate")
    long countByResourceIdAndDateBetween(@Param("resourceId") Long resourceId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
