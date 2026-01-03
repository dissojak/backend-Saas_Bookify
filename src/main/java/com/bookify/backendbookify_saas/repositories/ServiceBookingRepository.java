package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.BusinessClient;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.ServiceBooking;
import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for ServiceBooking with diagram-aligned queries.
 */
@Repository
public interface ServiceBookingRepository extends JpaRepository<ServiceBooking, Long> {

    /**
     * Find bookings by BusinessClient
     */
    List<ServiceBooking> findByBusinessClient(BusinessClient businessClient);

    List<ServiceBooking> findByClient(User client);

    /**
     * Find bookings by service and date
     */
    List<ServiceBooking> findByServiceAndDate(Service service, LocalDate date);

    List<ServiceBooking> findByService(Service service);

    /**
     * Find bookings for a business between dates
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.service.business.id = :businessId AND sb.date BETWEEN :startDate AND :endDate")
    List<ServiceBooking> findByServiceBusinessIdAndDateBetween(@Param("businessId") Long businessId,
                                                                @Param("startDate") LocalDate startDate,
                                                                @Param("endDate") LocalDate endDate);

    List<ServiceBooking> findByStatus(BookingStatusEnum status);

    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.service.business.id = :businessId AND sb.startTime BETWEEN :start AND :end AND sb.status <> com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CANCELLED")
    List<ServiceBooking> findForBusinessBetweenDates(@Param("businessId") Long businessId,
                                                     @Param("start") LocalDateTime start,
                                                     @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(sb) > 0 FROM ServiceBooking sb WHERE sb.service = :service AND sb.startTime < :end AND sb.endTime > :start")
    boolean existsOverlapping(@Param("service") Service service,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);

    /**
     * Find all bookings for a specific staff member on a specific date.
     * Excludes cancelled bookings.
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.staff.id = :staffId AND sb.date = :date AND sb.status <> com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CANCELLED")
    List<ServiceBooking> findByStaffIdAndDateExcludingCancelled(@Param("staffId") Long staffId, @Param("date") LocalDate date);

    /**
     * Find all bookings for a specific staff member within a date range.
     * Excludes cancelled bookings.
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.staff.id = :staffId AND sb.date BETWEEN :startDate AND :endDate AND sb.status <> com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CANCELLED")
    List<ServiceBooking> findByStaffIdAndDateBetweenExcludingCancelled(@Param("staffId") Long staffId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find all bookings for a specific staff member within a date range (including all statuses).
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.staff.id = :staffId AND sb.date BETWEEN :startDate AND :endDate")
    List<ServiceBooking> findByStaffIdAndDateBetween(@Param("staffId") Long staffId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Check for overlapping bookings for a staff member (correct conflict detection).
     * Uses LocalTime for start/end since Booking entity uses LocalTime.
     */
    @Query("SELECT COUNT(sb) > 0 FROM ServiceBooking sb WHERE sb.staff.id = :staffId AND sb.date = :date AND sb.startTime < :endTime AND sb.endTime > :startTime AND sb.status <> com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CANCELLED")
    boolean existsOverlappingForStaff(@Param("staffId") Long staffId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    /**
     * Find bookings by client (User) ID
     */
    @Query("SELECT sb FROM ServiceBooking sb WHERE sb.client.id = :clientId")
    List<ServiceBooking> findByClientId(@Param("clientId") Long clientId);

    /**
     * Find booking by ID with Service and Business eagerly loaded.
     * Explicitly fetch the business to avoid LAZY loading issues.
     */
    @Query("SELECT sb FROM ServiceBooking sb " +
           "LEFT JOIN FETCH sb.service s " +
           "LEFT JOIN FETCH s.business " +
           "LEFT JOIN FETCH sb.client " +
           "LEFT JOIN FETCH sb.staff " +
           "WHERE sb.id = :bookingId")
    java.util.Optional<ServiceBooking> findByIdWithServiceAndBusiness(@Param("bookingId") Long bookingId);

    /**
     * Find bookings by client (User) ID with Service and Business eagerly loaded.
     * Explicitly fetch the business to avoid LAZY loading issues.
     */
    @Query("SELECT DISTINCT sb FROM ServiceBooking sb " +
           "LEFT JOIN FETCH sb.service s " +
           "LEFT JOIN FETCH s.business " +
           "LEFT JOIN FETCH sb.client " +
           "LEFT JOIN FETCH sb.staff " +
           "WHERE sb.client.id = :clientId")
    List<ServiceBooking> findByClientIdWithServiceAndBusiness(@Param("clientId") Long clientId);

    /**
     * Count all bookings for a specific service (for delete safety check)
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.service.id = :serviceId")
    long countByServiceId(@Param("serviceId") Long serviceId);

    /**
     * Count pending/confirmed bookings for a specific service (active bookings)
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.service.id = :serviceId AND sb.status IN (com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.PENDING, com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CONFIRMED)")
    long countActiveBookingsByServiceId(@Param("serviceId") Long serviceId);

    /**
     * Count all bookings for a specific business client (for delete safety check)
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.businessClient.id = :businessClientId")
    long countByBusinessClientId(@Param("businessClientId") Long businessClientId);

    /**
     * Count bookings for a specific business client with specific statuses
     */
    @Query("SELECT COUNT(sb) FROM ServiceBooking sb WHERE sb.businessClient.id = :businessClientId AND sb.status IN :statuses")
    long countByBusinessClientIdAndStatusIn(@Param("businessClientId") Long businessClientId, @Param("statuses") java.util.List<com.bookify.backendbookify_saas.models.enums.BookingStatusEnum> statuses);
    
       /**
        * Delete all bookings for a specific business client
        */
       @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
       void deleteByBusinessClientId(Long businessClientId);
    
       /**
        * Delete all bookings for a specific business client with specific statuses
        */
       @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
       @Query("DELETE FROM ServiceBooking sb WHERE sb.businessClient.id = :businessClientId AND sb.status IN :statuses")
       void deleteByBusinessClientIdAndStatusIn(@Param("businessClientId") Long businessClientId, @Param("statuses") java.util.List<com.bookify.backendbookify_saas.models.enums.BookingStatusEnum> statuses);
}
