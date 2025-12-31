package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.ServiceBookingCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.ServiceBookingResponse;
import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import com.bookify.backendbookify_saas.services.impl.BookingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Booking operations.
 * Exposes endpoints for creating, retrieving, and managing bookings.
 */
@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Endpoints for managing bookings")
@Slf4j
public class BookingController {

    private final BookingServiceImpl bookingService;

    /**
     * Create a new booking.
     * Backend performs full validation: availability, conflicts, staff assignment.
     */
    @PostMapping
    @Operation(summary = "Create a new booking", description = "Creates a service booking with server-side validation for availability and conflicts")
    public ResponseEntity<?> createBooking(
            Authentication authentication,
            @Valid @RequestBody ServiceBookingCreateRequest request
    ) {
        try {
            log.info("Creating booking: serviceId={}, staffId={}, date={}, startTime={}",
                    request.getServiceId(), request.getStaffId(), request.getDate(), request.getStartTime());
            
            ServiceBookingResponse response = bookingService.createServiceBookingFromRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            log.error("Booking creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get a booking by ID.
     */
    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID", description = "Retrieve a specific booking by its ID")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        return bookingService.getServiceBookingById(bookingId)
                .map(booking -> ResponseEntity.ok(bookingService.mapToPublicResponse(booking)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get bookings for a staff member on a specific date.
     * Used by frontend to filter out occupied time slots.
     * Returns empty list if no bookings found (not an error).
     */
    @GetMapping("/staff/{staffId}/date/{date}")
    @Operation(summary = "Get bookings for staff on date", description = "Returns all non-cancelled bookings for a staff member on a specific date")
    public ResponseEntity<List<ServiceBookingResponse>> getBookingsForStaffOnDate(
            @PathVariable Long staffId,
            @PathVariable String date
    ) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<ServiceBookingResponse> bookings = bookingService.getBookingsForStaffOnDate(staffId, localDate);
            // Always return OK with the list (may be empty)
            return ResponseEntity.ok(bookings != null ? bookings : List.of());
        } catch (java.time.format.DateTimeParseException e) {
            log.warn("Invalid date format for staff {} date {}: {}", staffId, date, e.getMessage());
            return ResponseEntity.ok(List.of()); // Return empty list for bad date format
        } catch (Exception e) {
            log.error("Error fetching bookings for staff {} on date {}: {}", staffId, date, e.getMessage(), e);
            // Return empty list instead of 400 - this is a read operation for slot filtering
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get bookings for a business within a date range.
     */
    @GetMapping("/business/{businessId}")
    @Operation(summary = "Get bookings for business", description = "Returns all bookings for a business within an optional date range")
    public ResponseEntity<List<ServiceBookingResponse>> getBookingsForBusiness(
            @PathVariable Long businessId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to
    ) {
        try {
            LocalDate startDate = from != null ? LocalDate.parse(from) : LocalDate.now().minusMonths(1);
            LocalDate endDate = to != null ? LocalDate.parse(to) : LocalDate.now().plusMonths(1);
            List<ServiceBookingResponse> bookings = bookingService.getBookingsForBusinessBetweenDatesDto(businessId, startDate, endDate);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching bookings for business {}: {}", businessId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get bookings for the authenticated client.
     */
    @GetMapping("/my-bookings")
    @Operation(summary = "Get my bookings", description = "Returns all bookings for the authenticated user")
    public ResponseEntity<?> getMyBookings(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
        }
        
        try {
            Long userId = Long.parseLong(authentication.getName());
            List<ServiceBookingResponse> bookings = bookingService.getBookingsForClientDto(userId);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            log.error("Error fetching bookings for user: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Update booking status.
     */
    @PutMapping("/{bookingId}/status")
    @Operation(summary = "Update booking status", description = "Update the status of a booking (PENDING, CONFIRMED, CANCELLED, COMPLETED, NO_SHOW)")
    public ResponseEntity<?> updateBookingStatus(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String statusStr = body.get("status");
            if (statusStr == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            
            BookingStatusEnum status = BookingStatusEnum.valueOf(statusStr.toUpperCase());
            var updated = bookingService.updateBookingStatus(bookingId, status);
            return ResponseEntity.ok(bookingService.mapToPublicResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status value"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Cancel a booking.
     */
    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking", description = "Cancel a booking with an optional reason")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String reason = body != null ? body.get("reason") : null;
            bookingService.cancelServiceBooking(bookingId, reason);
            return ResponseEntity.ok(Map.of("message", "Booking cancelled successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Reschedule a booking to a new date/time.
     */
    @PutMapping("/{bookingId}/reschedule")
    @Operation(summary = "Reschedule a booking", description = "Reschedule a booking to a new date and time")
    public ResponseEntity<?> rescheduleBooking(
            @PathVariable Long bookingId,
            @RequestBody Map<String, String> body
    ) {
        try {
            String newDate = body.get("date");
            String newStartTime = body.get("startTime");
            String newEndTime = body.get("endTime");
            
            if (newDate == null || newStartTime == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Date and start time are required"));
            }
            
            LocalDate date = LocalDate.parse(newDate);
            java.time.LocalTime startTime = java.time.LocalTime.parse(newStartTime);
            java.time.LocalTime endTime = newEndTime != null 
                ? java.time.LocalTime.parse(newEndTime) 
                : null;
            
            ServiceBookingResponse response = bookingService.rescheduleBooking(bookingId, date, startTime, endTime);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            log.error("Reschedule failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
