package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for public resource endpoints
 * Clients can search, view, and book resources
 * Path: /v1/resources
 */
@RestController
@RequestMapping("/v1/resources")
@RequiredArgsConstructor
public class ResourcePublicController {

    private final ResourceService resourceService;
    private final ResourceAvailabilityService availabilityService;
    private final ResourceReservationService reservationService;

    // ============================================================================
    // SEARCH ENDPOINTS
    // ============================================================================

    /**
     * Search resources independently (not via business)
     * GET /v1/resources/search?query=&type=&minPrice=&maxPrice=
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ResourceResponse>> searchResources(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice,
            Pageable pageable) {
        
        Page<ResourceResponse> results;
        
        if (type != null && !type.isEmpty()) {
            results = resourceService.searchResourcesByType(query, type, pageable);
        } else {
            results = resourceService.searchResources(query, pageable);
        }
        
        return ResponseEntity.ok(results);
    }

    // ============================================================================
    // DETAIL ENDPOINTS
    // ============================================================================

    /**
     * Get resource details with all attributes and pricing options
     * GET /v1/resources/{resourceId}
     */
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResource(
            @PathVariable Long resourceId) {
        ResourceResponse resource = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(resource);
    }

    // ============================================================================
    // AVAILABILITY ENDPOINTS
    // ============================================================================

    /**
     * Get available slots for a resource
     * GET /v1/resources/{resourceId}/availabilities?from=&to=
     */
    @GetMapping("/{resourceId}/availabilities")
    public ResponseEntity<List<ResourceAvailabilityDTO>> getAvailableSlots(
            @PathVariable Long resourceId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        List<ResourceAvailabilityDTO> slots = availabilityService.getAvailabilitiesByResource(resourceId, from, to);
        return ResponseEntity.ok(slots);
    }

    // ============================================================================
    // RESERVATION ENDPOINTS
    // ============================================================================

    /**
     * Create a resource reservation (book a resource)
     * POST /v1/resources/{resourceId}/reservations
     */
    @PostMapping("/{resourceId}/reservations")
    public ResponseEntity<ResourceReservationResponse> createReservation(
            @PathVariable Long resourceId,
            @RequestBody ResourceReservationRequest request,
            @RequestHeader("X-User-ID") Long clientId) {
        
        request.setResourceId(resourceId);
        ResourceReservationResponse response = reservationService.createReservation(clientId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get current client's reservations
     * GET /v1/resources/reservations/my
     */
    @GetMapping("/reservations/my")
    public ResponseEntity<List<ResourceReservationResponse>> getMyReservations(
            @RequestHeader("X-User-ID") Long clientId) {
        List<ResourceReservationResponse> reservations = reservationService.getReservationsByClient(clientId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Cancel a resource reservation
     * DELETE /v1/resources/reservations/{reservationId}
     */
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId,
            @RequestHeader("X-User-ID") Long clientId) {
        reservationService.cancelReservation(reservationId, clientId);
        return ResponseEntity.noContent().build();
    }
}
