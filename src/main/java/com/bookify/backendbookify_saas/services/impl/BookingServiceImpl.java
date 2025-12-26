package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.ServiceBookingCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.ServiceBookingResponse;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.models.enums.AvailabilityStatus;
import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import com.bookify.backendbookify_saas.repositories.*;
import com.bookify.backendbookify_saas.services.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BookingService with support for both User and BusinessClient.
 * Conflict detection is staff-based, not service-based.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ServiceBookingRepository serviceBookingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final BusinessClientRepository businessClientRepository;
    private final BookingRepository bookingRepository;
    private final StaffRepository staffRepository;
    private final StaffAvailabilityRepository staffAvailabilityRepository;

    @Override
    @Transactional
    public ServiceBooking createServiceBooking(ServiceBooking booking) {
        return serviceBookingRepository.save(booking);
    }

    /**
     * Create a service booking with full validation:
     * - Staff availability check
     * - Staff conflict detection (not service-based)
     * - Business client validation
     */
    @Transactional
    public ServiceBookingResponse createServiceBookingFromRequest(ServiceBookingCreateRequest request) {
        // Validate that either clientId or businessClientId is provided
        if (request.getClientId() == null && request.getBusinessClientId() == null) {
            throw new RuntimeException("Either clientId or businessClientId must be provided");
        }
        if (request.getClientId() != null && request.getBusinessClientId() != null) {
            throw new RuntimeException("Only one of clientId or businessClientId can be provided");
        }

        // Staff is required for booking
        if (request.getStaffId() == null) {
            throw new RuntimeException("Staff ID is required for booking");
        }

        // Fetch service
        com.bookify.backendbookify_saas.models.entities.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service not found"));

        // Fetch staff with business eagerly loaded
        log.info("Attempting to fetch staff with ID: {}", request.getStaffId());
        Staff staff = staffRepository.findByIdWithBusiness(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        
        log.info("Found staff: ID={}, Name={}, Business={}", 
                staff.getId(), 
                staff.getName(), 
                staff.getBusiness() != null ? staff.getBusiness().getId() : "NULL");

        // Validate staff belongs to the same business as the service
        if (staff.getBusiness() == null) {
            throw new RuntimeException("Staff does not have a business associated. Staff ID: " + staff.getId());
        }
        if (!staff.getBusiness().getId().equals(service.getBusiness().getId())) {
            throw new RuntimeException("Staff does not belong to the same business as the service");
        }

        // 1. Check staff availability for the date
        Optional<StaffAvailability> availabilityOpt = staffAvailabilityRepository.findByStaff_IdAndDate(
                request.getStaffId(), request.getDate());
        
        if (availabilityOpt.isEmpty()) {
            throw new RuntimeException("Staff has no availability data for this date");
        }
        
        StaffAvailability availability = availabilityOpt.get();
        if (availability.getStatus() != AvailabilityStatus.AVAILABLE) {
            throw new RuntimeException("Staff is not available on this date (status: " + availability.getStatus() + ")");
        }

        // 2. Check booking time is within staff working hours
        if (request.getStartTime().isBefore(availability.getStartTime()) ||
            request.getEndTime().isAfter(availability.getEndTime())) {
            throw new RuntimeException("Booking time is outside staff working hours (" + 
                    availability.getStartTime() + " - " + availability.getEndTime() + ")");
        }

        // 3. Check for conflicting bookings (STAFF-BASED, not service-based)
        boolean hasConflict = serviceBookingRepository.existsOverlappingForStaff(
                request.getStaffId(),
                request.getDate(),
                request.getStartTime(),
                request.getEndTime()
        );
        
        if (hasConflict) {
            throw new RuntimeException("Time slot is already booked for this staff member");
        }

        // Build the booking
        ServiceBooking booking = new ServiceBooking();
        booking.setService(service);
        booking.setStaff(staff);
        booking.setDate(request.getDate());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setNotes(request.getNotes());
        booking.setPrice(request.getPrice());
        booking.setStatus(request.getStatus() != null ? request.getStatus() : BookingStatusEnum.PENDING);

        // Handle User client
        if (request.getClientId() != null) {
            User client = userRepository.findById(request.getClientId())
                    .orElseThrow(() -> new RuntimeException("User client not found"));
            booking.setClient(client);
        }

        // Handle BusinessClient
        if (request.getBusinessClientId() != null) {
            BusinessClient businessClient = businessClientRepository.findById(request.getBusinessClientId())
                    .orElseThrow(() -> new RuntimeException("Business client not found"));

            // Validate that the BusinessClient belongs to the same business as the service
            if (!businessClient.getBusiness().getId().equals(service.getBusiness().getId())) {
                throw new RuntimeException("Business client does not belong to the same business as the service");
            }

            booking.setBusinessClient(businessClient);
        }

        log.info("Creating booking: staffId={}, date={}, time={}-{}", 
                staff.getId(), request.getDate(), request.getStartTime(), request.getEndTime());

        ServiceBooking savedBooking = serviceBookingRepository.save(booking);
        return mapToResponse(savedBooking);
    }

    @Override
    @Transactional
    public ServiceBooking updateServiceBooking(Long id, ServiceBooking booking) {
        ServiceBooking existing = serviceBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        existing.setDate(booking.getDate());
        existing.setStartTime(booking.getStartTime());
        existing.setEndTime(booking.getEndTime());
        existing.setStatus(booking.getStatus());
        existing.setNotes(booking.getNotes());
        existing.setPrice(booking.getPrice());

        return serviceBookingRepository.save(existing);
    }

    @Override
    @Transactional
    public ServiceBooking updateBookingStatus(Long id, BookingStatusEnum status) {
        ServiceBooking booking = serviceBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(status);
        return serviceBookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ServiceBooking> getServiceBookingById(Long id) {
        return serviceBookingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceBooking> getBookingsByClient(User client) {
        return serviceBookingRepository.findByClient(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceBooking> getBookingsByService(com.bookify.backendbookify_saas.models.entities.Service service) {
        return serviceBookingRepository.findByService(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceBooking> getBookingsByStatus(BookingStatusEnum status) {
        return serviceBookingRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceBooking> getBookingsForBusinessBetweenDates(Long businessId, LocalDate startDate, LocalDate endDate) {
        // Query all bookings and filter by business and date range
        return serviceBookingRepository.findAll().stream()
                .filter(booking -> booking.getService().getBusiness().getId().equals(businessId))
                .filter(booking -> !booking.getDate().isBefore(startDate) && !booking.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    /**
     * Get bookings by BusinessClient
     */
    @Transactional(readOnly = true)
    public List<ServiceBookingResponse> getBookingsByBusinessClient(Long businessClientId) {
        BusinessClient businessClient = businessClientRepository.findById(businessClientId)
                .orElseThrow(() -> new RuntimeException("Business client not found"));

        // Query all bookings and filter by business client
        List<ServiceBooking> bookings = serviceBookingRepository.findAll().stream()
                .filter(booking -> booking.getBusinessClient() != null &&
                        booking.getBusinessClient().getId().equals(businessClientId))
                .collect(Collectors.toList());

        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTimeSlotAvailable(com.bookify.backendbookify_saas.models.entities.Service service, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Query bookings by service and filter by date
        List<ServiceBooking> existingBookings = serviceBookingRepository.findByService(service).stream()
                .filter(booking -> booking.getDate().equals(date))
                .collect(Collectors.toList());

        for (ServiceBooking booking : existingBookings) {
            if (booking.getStatus() == BookingStatusEnum.CANCELLED) {
                continue;
            }

            // Check for time overlap
            if (startTime.isBefore(booking.getEndTime()) && endTime.isAfter(booking.getStartTime())) {
                return false; // Overlap detected
            }
        }

        return true;
    }

    @Override
    @Transactional
    public void cancelServiceBooking(Long id, String reason) {
        ServiceBooking booking = serviceBookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setStatus(BookingStatusEnum.CANCELLED);
        if (reason != null && !reason.isBlank()) {
            booking.setNotes((booking.getNotes() != null ? booking.getNotes() + "\n" : "") + "Cancellation reason: " + reason);
        }
        serviceBookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void deleteServiceBooking(Long id) {
        if (!serviceBookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found");
        }
        serviceBookingRepository.deleteById(id);
    }

    /**
     * Map ServiceBooking entity to response DTO
     */
    private ServiceBookingResponse mapToResponse(ServiceBooking booking) {
        ServiceBookingResponse.ServiceBookingResponseBuilder builder = ServiceBookingResponse.builder()
                .id(booking.getId())
                .serviceId(booking.getService().getId())
                .serviceName(booking.getService().getName())
                .date(booking.getDate())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .price(booking.getPrice())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt());

        // Set client information based on type
        if (booking.getClient() != null) {
            builder.clientId(booking.getClient().getId())
                    .clientName(booking.getClient().getName())
                    .clientEmail(booking.getClient().getEmail())
                    .clientType("USER");
        } else if (booking.getBusinessClient() != null) {
            builder.clientId(booking.getBusinessClient().getId())
                    .clientName(booking.getBusinessClient().getName())
                    .clientEmail(booking.getBusinessClient().getEmail())
                    .clientType("BUSINESS_CLIENT");
        }

        // Set staff information if available
        if (booking.getStaff() != null) {
            builder.staffId(booking.getStaff().getId())
                    .staffName(booking.getStaff().getName());
        }

        return builder.build();
    }

    /**
     * Public method to map booking to response (used by controller).
     */
    public ServiceBookingResponse mapToPublicResponse(ServiceBooking booking) {
        return mapToResponse(booking);
    }

    /**
     * Get all bookings for a staff member on a specific date.
     * Returns non-cancelled bookings only.
     */
    @Transactional(readOnly = true)
    public List<ServiceBookingResponse> getBookingsForStaffOnDate(Long staffId, LocalDate date) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByStaffIdAndDateExcludingCancelled(staffId, date);
        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings for a business between dates as DTOs.
     */
    @Transactional(readOnly = true)
    public List<ServiceBookingResponse> getBookingsForBusinessBetweenDatesDto(Long businessId, LocalDate startDate, LocalDate endDate) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByServiceBusinessIdAndDateBetween(businessId, startDate, endDate);
        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get bookings for a client (User) as DTOs.
     */
    @Transactional(readOnly = true)
    public List<ServiceBookingResponse> getBookingsForClientDto(Long clientId) {
        List<ServiceBooking> bookings = serviceBookingRepository.findByClientId(clientId);
        return bookings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}