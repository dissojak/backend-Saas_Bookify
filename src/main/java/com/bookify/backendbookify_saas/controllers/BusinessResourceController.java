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
 * REST Controller for managing resources (for Business Owners)
 * Owners have full access to create, update, delete resources and assign staff
 * Path: /v1/businesses/{businessId}/resources
 */
@RestController
@RequestMapping("/v1/businesses/{businessId}/resources")
@RequiredArgsConstructor
public class BusinessResourceController {

    private final ResourceService resourceService;
    private final ResourceAvailabilityService availabilityService;
    private final ResourceReservationService reservationService;

    // ============================================================================
    // RESOURCE CRUD ENDPOINTS
    // ============================================================================

    /**
     * Create a new resource
     * POST /v1/businesses/{businessId}/resources
     */
    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(
            @PathVariable Long businessId,
            @RequestBody ResourceCreateRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        ResourceResponse response = resourceService.createResource(businessId, userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all resources for a business
     * GET /v1/businesses/{businessId}/resources
     */
    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getResources(
            @PathVariable Long businessId) {
        List<ResourceResponse> resources = resourceService.getResourcesByBusiness(businessId);
        return ResponseEntity.ok(resources);
    }

    /**
     * Get a specific resource
     * GET /v1/businesses/{businessId}/resources/{resourceId}
     */
    @GetMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> getResource(
            @PathVariable Long businessId,
            @PathVariable Long resourceId) {
        ResourceResponse resource = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(resource);
    }

    /**
     * Update a resource
     * PUT /v1/businesses/{businessId}/resources/{resourceId}
     */
    @PutMapping("/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestBody ResourceUpdateRequest request,
            @RequestHeader("X-User-ID") Long userId) {
        ResourceResponse response = resourceService.updateResource(resourceId, businessId, userId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a resource
     * DELETE /v1/businesses/{businessId}/resources/{resourceId}
     */
    @DeleteMapping("/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestHeader("X-User-ID") Long userId) {
        resourceService.deleteResource(resourceId, businessId, userId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // STAFF ASSIGNMENT ENDPOINTS
    // ============================================================================

    /**
     * Assign staff to a resource
     * POST /v1/businesses/{businessId}/resources/{resourceId}/staff/{staffId}
     */
    @PostMapping("/{resourceId}/staff/{staffId}")
    public ResponseEntity<Void> assignStaffToResource(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long staffId,
            @RequestHeader("X-User-ID") Long userId) {
        resourceService.assignStaffToResource(resourceId, staffId, userId, businessId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove staff from a resource
     * DELETE /v1/businesses/{businessId}/resources/{resourceId}/staff/{staffId}
     */
    @DeleteMapping("/{resourceId}/staff/{staffId}")
    public ResponseEntity<Void> removeStaffFromResource(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long staffId,
            @RequestHeader("X-User-ID") Long userId) {
        resourceService.removeStaffFromResource(resourceId, staffId, userId, businessId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // IMAGE ENDPOINTS
    // ============================================================================

    /**
     * Add an image to a resource
     * POST /v1/businesses/{businessId}/resources/{resourceId}/images
     */
    @PostMapping("/{resourceId}/images")
    public ResponseEntity<ResourceImageDTO> addImage(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestParam String imageUrl,
            @RequestParam(defaultValue = "false") boolean isPrimary) {
        ResourceImageDTO image = resourceService.addImage(resourceId, imageUrl, isPrimary);
        return new ResponseEntity<>(image, HttpStatus.CREATED);
    }

    /**
     * Remove an image from a resource
     * DELETE /v1/businesses/{businessId}/resources/{resourceId}/images/{imageId}
     */
    @DeleteMapping("/{resourceId}/images/{imageId}")
    public ResponseEntity<Void> removeImage(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long imageId) {
        resourceService.removeImage(resourceId, imageId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Reorder images
     * PUT /v1/businesses/{businessId}/resources/{resourceId}/images/reorder
     */
    @PutMapping("/{resourceId}/images/reorder")
    public ResponseEntity<Void> reorderImages(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestBody List<Long> imageIds) {
        resourceService.reorderImages(resourceId, imageIds);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // AVAILABILITY ENDPOINTS
    // ============================================================================

    /**
     * Create a single availability slot
     * POST /v1/businesses/{businessId}/resources/{resourceId}/availabilities
     */
    @PostMapping("/{resourceId}/availabilities")
    public ResponseEntity<ResourceAvailabilityDTO> createAvailability(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestBody ResourceAvailabilityRequest request) {
        ResourceAvailabilityDTO availability = availabilityService.createAvailability(resourceId, request);
        return new ResponseEntity<>(availability, HttpStatus.CREATED);
    }

    /**
     * Create multiple availability slots in bulk
     * POST /v1/businesses/{businessId}/resources/{resourceId}/availabilities/bulk
     */
    @PostMapping("/{resourceId}/availabilities/bulk")
    public ResponseEntity<List<ResourceAvailabilityDTO>> createBulkAvailabilities(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestBody ResourceAvailabilityBulkRequest request) {
        List<ResourceAvailabilityDTO> availabilities = availabilityService.generateBulkAvailabilities(resourceId, request);
        return new ResponseEntity<>(availabilities, HttpStatus.CREATED);
    }

    /**
     * Get availabilities for a resource
     * GET /v1/businesses/{businessId}/resources/{resourceId}/availabilities?from=&to=
     */
    @GetMapping("/{resourceId}/availabilities")
    public ResponseEntity<List<ResourceAvailabilityDTO>> getAvailabilities(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @RequestParam LocalDate from,
            @RequestParam LocalDate to) {
        List<ResourceAvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByResource(resourceId, from, to);
        return ResponseEntity.ok(availabilities);
    }

    /**
     * Update an availability slot
     * PUT /v1/businesses/{businessId}/resources/{resourceId}/availabilities/{availabilityId}
     */
    @PutMapping("/{resourceId}/availabilities/{availabilityId}")
    public ResponseEntity<ResourceAvailabilityDTO> updateAvailability(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long availabilityId,
            @RequestBody ResourceAvailabilityRequest request) {
        ResourceAvailabilityDTO availability = availabilityService.updateAvailability(availabilityId, request);
        return ResponseEntity.ok(availability);
    }

    /**
     * Delete an availability slot
     * DELETE /v1/businesses/{businessId}/resources/{resourceId}/availabilities/{availabilityId}
     */
    @DeleteMapping("/{resourceId}/availabilities/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long availabilityId) {
        availabilityService.deleteAvailability(availabilityId);
        return ResponseEntity.noContent().build();
    }

    // ============================================================================
    // RESERVATION ENDPOINTS
    // ============================================================================

    /**
     * Get reservations for a resource
     * GET /v1/businesses/{businessId}/resources/{resourceId}/reservations
     */
    @GetMapping("/{resourceId}/reservations")
    public ResponseEntity<List<ResourceReservationResponse>> getReservations(
            @PathVariable Long businessId,
            @PathVariable Long resourceId) {
        List<ResourceReservationResponse> reservations = reservationService.getReservationsByResource(resourceId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * Confirm a reservation
     * PUT /v1/businesses/{businessId}/resources/{resourceId}/reservations/{reservationId}/confirm
     */
    @PutMapping("/{resourceId}/reservations/{reservationId}/confirm")
    public ResponseEntity<ResourceReservationResponse> confirmReservation(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long reservationId,
            @RequestHeader("X-User-ID") Long userId) {
        ResourceReservationResponse response = reservationService.confirmReservation(reservationId, userId);
        return ResponseEntity.ok(response);
    }

    /**
     * Complete a reservation
     * PUT /v1/businesses/{businessId}/resources/{resourceId}/reservations/{reservationId}/complete
     */
    @PutMapping("/{resourceId}/reservations/{reservationId}/complete")
    public ResponseEntity<ResourceReservationResponse> completeReservation(
            @PathVariable Long businessId,
            @PathVariable Long resourceId,
            @PathVariable Long reservationId,
            @RequestHeader("X-User-ID") Long userId) {
        ResourceReservationResponse response = reservationService.completeReservation(reservationId, userId);
        return ResponseEntity.ok(response);
    }
}
