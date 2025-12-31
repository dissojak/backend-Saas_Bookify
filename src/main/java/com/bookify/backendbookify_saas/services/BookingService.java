package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.ServiceBookingResponse;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.ServiceBooking;
import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing bookings
 */
public interface BookingService {

    ServiceBooking createServiceBooking(ServiceBooking booking);

    ServiceBooking updateServiceBooking(Long id, ServiceBooking booking);

    ServiceBooking updateBookingStatus(Long id, BookingStatusEnum status);

    Optional<ServiceBooking> getServiceBookingById(Long id);

    List<ServiceBooking> getBookingsByClient(User client);

    List<ServiceBooking> getBookingsByService(Service service);

    List<ServiceBooking> getBookingsByStatus(BookingStatusEnum status);

    List<ServiceBooking> getBookingsForBusinessBetweenDates(Long businessId, LocalDate startDate, LocalDate endDate);

    boolean isTimeSlotAvailable(Service service, LocalDate date, LocalTime startTime, LocalTime endTime);

    void cancelServiceBooking(Long id, String reason);

    void deleteServiceBooking(Long id);

    ServiceBookingResponse rescheduleBooking(Long bookingId, LocalDate newDate, LocalTime newStartTime, LocalTime newEndTime);
}
