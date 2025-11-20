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
}
