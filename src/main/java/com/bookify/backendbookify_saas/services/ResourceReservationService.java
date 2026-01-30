package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.models.enums.BookingStatusEnum;
import com.bookify.backendbookify_saas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing resource reservations
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceReservationService {

    private final ResourceReservationRepository reservationRepository;
    private final ResourceAvailabilityRepository availabilityRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    /**
     * Create a new reservation for a resource
     */
    public ResourceReservationResponse createReservation(Long clientId, ResourceReservationRequest request) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        ResourceAvailability availability = availabilityRepository.findById(request.getAvailabilityId())
                .orElseThrow(() -> new RuntimeException("Availability slot not found"));

        Resource resource = availability.getResource();

        // Check if slot is available
        if (!"AVAILABLE".equals(availability.getStatus().name())) {
            throw new RuntimeException("Availability slot is not available");
        }

        // Check for conflicting reservations
        if (reservationRepository.existsActiveReservation(availability.getId())) {
            throw new RuntimeException("This slot is already booked");
        }

        // Get pricing option and calculate price
        ResourcePricingOption pricingOption = resource.getPricingOptions().stream()
                .filter(opt -> opt.getId().equals(request.getPricingOptionId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Pricing option not found"));

        BigDecimal price = pricingOption.getPrice();

        ResourceReservation reservation = new ResourceReservation();
        reservation.setClient(client);
        reservation.setResourceAvailability(availability);
        reservation.setDate(availability.getDate());
        reservation.setStartTime(availability.getStartTime());
        reservation.setEndTime(availability.getEndTime());
        reservation.setPrice(price);
        reservation.setStatus(BookingStatusEnum.PENDING);
        reservation.setNotes(request.getNotes());

        ResourceReservation saved = reservationRepository.save(reservation);
        return mapToResponse(saved);
    }

    /**
     * Cancel a reservation
     */
    public void cancelReservation(Long reservationId, Long actorId) {
        ResourceReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Only client who made the reservation or staff/owner of the resource can cancel
        if (!reservation.getClient().getId().equals(actorId)) {
            // Check if actor is staff or owner of the resource
            Resource resource = reservation.getResourceAvailability().getResource();
            boolean isOwner = resource.getBusiness().getOwner().getId().equals(actorId);
            boolean isStaff = resource.getAssignedStaff().stream()
                    .anyMatch(s -> s.getId().equals(actorId));

            if (!isOwner && !isStaff) {
                throw new RuntimeException("Permission denied");
            }
        }

        reservation.setStatus(BookingStatusEnum.CANCELLED);
        reservationRepository.save(reservation);
    }

    /**
     * Confirm a reservation (staff/owner only)
     */
    public ResourceReservationResponse confirmReservation(Long reservationId, Long actorId) {
        ResourceReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Check permission
        Resource resource = reservation.getResourceAvailability().getResource();
        boolean isOwner = resource.getBusiness().getOwner().getId().equals(actorId);
        boolean isStaff = resource.getAssignedStaff().stream()
                .anyMatch(s -> s.getId().equals(actorId));

        if (!isOwner && !isStaff) {
            throw new RuntimeException("Permission denied");
        }

        reservation.setStatus(BookingStatusEnum.CONFIRMED);
        ResourceReservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    /**
     * Complete a reservation (staff/owner only)
     */
    public ResourceReservationResponse completeReservation(Long reservationId, Long actorId) {
        ResourceReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // Check permission
        Resource resource = reservation.getResourceAvailability().getResource();
        boolean isOwner = resource.getBusiness().getOwner().getId().equals(actorId);
        boolean isStaff = resource.getAssignedStaff().stream()
                .anyMatch(s -> s.getId().equals(actorId));

        if (!isOwner && !isStaff) {
            throw new RuntimeException("Permission denied");
        }

        reservation.setStatus(BookingStatusEnum.COMPLETED);
        ResourceReservation updated = reservationRepository.save(reservation);
        return mapToResponse(updated);
    }

    /**
     * Get reservations for a client
     */
    public List<ResourceReservationResponse> getReservationsByClient(Long clientId) {
        return reservationRepository.findByClientId(clientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reservations for a business
     */
    public List<ResourceReservationResponse> getReservationsByBusiness(Long businessId) {
        return reservationRepository.findByBusinessId(businessId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get reservations for a specific resource
     */
    public List<ResourceReservationResponse> getReservationsByResource(Long resourceId) {
        return reservationRepository.findByResourceId(resourceId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ResourceReservationResponse mapToResponse(ResourceReservation reservation) {
        ResourceAvailability availability = reservation.getResourceAvailability();
        Resource resource = availability.getResource();
        String primaryImageUrl = resource.getPrimaryImage() != null ? resource.getPrimaryImage().getImageUrl() : null;

        return ResourceReservationResponse.builder()
                .id(reservation.getId())
                .resourceId(resource.getId())
                .resourceName(resource.getName())
                .resourcePrimaryImage(primaryImageUrl)
                .clientId(reservation.getClient().getId())
                .clientName(reservation.getClient().getName())
                .date(availability.getDate())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .price(reservation.getPrice())
                .pricingType("RESOURCE_RESERVATION")
                .status(reservation.getStatus().name())
                .notes(reservation.getNotes())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
}
