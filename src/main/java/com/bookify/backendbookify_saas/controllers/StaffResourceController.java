package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for staff resource management
 * Staff can only manage resources they are assigned to
 * They cannot change staff assignments or delete resources
 * Path: /v1/staff/resources
 */
@RestController
@RequestMapping("/v1/staff/resources")
@RequiredArgsConstructor
public class StaffResourceController {

    private final ResourceService resourceService;
    private final ResourceAvailabilityService availabilityService;
    private final ResourceReservationService reservationService;
    private final StaffSecurityService staffSecurityService;

    // ============================================================================
    // RESOURCE VIEW/UPDATE ENDPOINTS
    // ============================================================================

    /**
     * Get all resources assigned to current staff
     * GET /v1/staff/resources
     */
    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAssignedResources(
            @RequestHeader("X-User-ID") Long staffId) {
        List<ResourceResponse> resources = resourceService.getResourcesByStaff(staffId);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get a specific resource (if assigned to staff)
     * GET /v1/staff/resources/{resourceId}
     */
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResource(
            @PathVariable Long resourceId,
            @RequestHeader("X-User-ID") Long staffId) {
        
        // Verify staff is assigned to this resource
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        ResourceResponse resource = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(resource);
    }

    /**
     * Update a resource (if assigned to staff)
     * Staff CANNOT change staff assignments or pricing
     * PUT /v1/staff/resources/{resourceId}
     */
    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long resourceId,
            @RequestBody ResourceUpdateRequest request,
            @RequestHeader("X-User-ID") Long staffId) {
        
        // Verify staff is assigned to this resource
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        // Staff cannot modify staff assignments or pricing options
        request.setStaffIds(null);
        request.setPricingOptions(null);
        
        ResourceResponse response = resourceService.updateResource(resourceId, null, staffId, request);
        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // AVAILABILITY ENDPOINTS
    // ============================================================================

    /**
     * Create a single availability slot (if assigned)
     * POST /v1/staff/resources/{resourceId}/availabilities
     */
    @PostMapping("/{resourceId}/availabilities")
    public ResponseEntity<ResourceAvailabilityDTO> createAvailability(
            @PathVariable Long resourceId,
            @RequestBody ResourceAvailabilityRequest request,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        ResourceAvailabilityDTO availability = availabilityService.createAvailability(resourceId, request);
        return new ResponseEntity<>(availability, HttpStatus.CREATED);
    }

    /**
     * Create bulk availability slots (if assigned)
     * POST /v1/staff/resources/{resourceId}/availabilities/bulk
     */
    @PostMapping("/{resourceId}/availabilities/bulk")
    public ResponseEntity<List<ResourceAvailabilityDTO>> createBulkAvailabilities(
            @PathVariable Long resourceId,
            @RequestBody ResourceAvailabilityBulkRequest request,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        List<ResourceAvailabilityDTO> availabilities = availabilityService.generateBulkAvailabilities(resourceId, request);
        return new ResponseEntity<>(availabilities, HttpStatus.CREATED);
    }

    /**
     * Get availabilities for a resource (if assigned)
     * GET /v1/staff/resources/{resourceId}/availabilities?from=&to=
     */
    @GetMapping("/{resourceId}/availabilities")
    public ResponseEntity<List<ResourceAvailabilityDTO>> getAvailabilities(
            @PathVariable Long resourceId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        List<ResourceAvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByResource(resourceId, from, to);
        return ResponseEntity.ok(availabilities);
    }

    /**
     * Update an availability slot (if assigned)
     * PUT /v1/staff/resources/{resourceId}/availabilities/{availabilityId}
     */
    @PutMapping("/{resourceId}/availabilities/{availabilityId}")
    public ResponseEntity<ResourceAvailabilityDTO> updateAvailability(
            @PathVariable Long resourceId,
            @PathVariable Long availabilityId,
            @RequestBody ResourceAvailabilityRequest request,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        ResourceAvailabilityDTO availability = availabilityService.updateAvailability(availabilityId, request);
        return ResponseEntity.ok(availability);
    }

    /**
     * Delete an availability slot (if assigned)
     * DELETE /v1/staff/resources/{resourceId}/availabilities/{availabilityId}
     */
    @DeleteMapping("/{resourceId}/availabilities/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long resourceId,
            @PathVariable Long availabilityId,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // RESERVATION ENDPOINTS
    // ============================================================================

    /**
     * Get reservations for a resource (if assigned)
     * GET /v1/staff/resources/{resourceId}/reservations
     */
    @GetMapping("/{resourceId}/reservations")
    public ResponseEntity<List<ResourceReservationResponse>> getReservations(
            @PathVariable Long resourceId,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        List<ResourceReservationResponse> reservations = reservationService.getReservationsByResource(resourceId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Confirm a reservation (if assigned)
     * PUT /v1/staff/resources/{resourceId}/reservations/{reservationId}/confirm
     */
    @PutMapping("/{resourceId}/reservations/{reservationId}/confirm")
    public ResponseEntity<ResourceReservationResponse> confirmReservation(
            @PathVariable Long resourceId,
            @PathVariable Long reservationId,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        ResourceReservationResponse response = reservationService.confirmReservation(reservationId, staffId);
        return ResponseEntity.ok(response);
    }

    /**
     * Complete a reservation (if assigned)
     * PUT /v1/staff/resources/{resourceId}/reservations/{reservationId}/complete
     */
    @PutMapping("/{resourceId}/reservations/{reservationId}/complete")
    public ResponseEntity<ResourceReservationResponse> completeReservation(
            @PathVariable Long resourceId,
            @PathVariable Long reservationId,
            @RequestHeader("X-User-ID") Long staffId) {
        
        if (!staffSecurityService.canManageResource(staffId, resourceId)) {
            return ResponseEntity.status(403).build();
        }
        
        ResourceReservationResponse response = reservationService.completeReservation(reservationId, staffId);
        return ResponseEntity.ok(response);
    }
}
