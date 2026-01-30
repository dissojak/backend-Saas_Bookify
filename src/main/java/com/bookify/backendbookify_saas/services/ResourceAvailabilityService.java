package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.models.enums.ResourceAvailabilityStatusEnum;
import com.bookify.backendbookify_saas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing resource availability slots
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceAvailabilityService {

    private final ResourceAvailabilityRepository availabilityRepository;
    private final ResourceRepository resourceRepository;

    /**
     * Create a single availability slot
     */
    public ResourceAvailabilityDTO createAvailability(Long resourceId, ResourceAvailabilityRequest request) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        ResourceAvailability availability = ResourceAvailability.builder()
                .date(request.getDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ResourceAvailabilityStatusEnum.valueOf(request.getStatus() != null ? request.getStatus() : "AVAILABLE"))
                .resource(resource)
                .build();

        ResourceAvailability saved = availabilityRepository.save(availability);
        return mapToDTO(saved);
    }

    /**
     * Update an availability slot
     */
    public ResourceAvailabilityDTO updateAvailability(Long availabilityId, ResourceAvailabilityRequest request) {
        ResourceAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));

        if (request.getDate() != null) availability.setDate(request.getDate());
        if (request.getStartTime() != null) availability.setStartTime(request.getStartTime());
        if (request.getEndTime() != null) availability.setEndTime(request.getEndTime());
        if (request.getStatus() != null) availability.setStatus(ResourceAvailabilityStatusEnum.valueOf(request.getStatus()));

        ResourceAvailability updated = availabilityRepository.save(availability);
        return mapToDTO(updated);
    }

    /**
     * Delete an availability slot
     */
    public void deleteAvailability(Long availabilityId) {
        ResourceAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new RuntimeException("Availability not found"));
        availabilityRepository.delete(availability);
    }

    /**
     * Get availabilities for a resource within a date range
     */
    public List<ResourceAvailabilityDTO> getAvailabilitiesByResource(Long resourceId, LocalDate from, LocalDate to) {
        return availabilityRepository.findByResourceIdAndDateBetween(resourceId, from, to).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get available slots for a specific date (only AVAILABLE status, not booked)
     */
    public List<ResourceAvailabilityDTO> getAvailableSlots(Long resourceId, LocalDate date) {
        return availabilityRepository.findAvailableSlots(resourceId, date).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generate bulk availability slots
     * Creates slots based on daily hours and slot duration
     */
    public List<ResourceAvailabilityDTO> generateBulkAvailabilities(Long resourceId, ResourceAvailabilityBulkRequest request) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        List<ResourceAvailability> slots = new ArrayList<>();
        LocalDate current = request.getStartDate();

        while (!current.isAfter(request.getEndDate())) {
            // Skip weekends if requested
            if (request.getExcludeWeekends() != null && request.getExcludeWeekends()) {
                if (current.getDayOfWeek() == DayOfWeek.SATURDAY || current.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    current = current.plusDays(1);
                    continue;
                }
            }

            // Skip excluded dates
            if (request.getExcludedDates() != null && request.getExcludedDates().contains(current)) {
                current = current.plusDays(1);
                continue;
            }

            // Create slots for this day
            LocalTime slotStart = request.getDailyStartTime();
            LocalTime slotEnd = slotStart.plusMinutes(request.getSlotDurationMinutes());

            while (!slotEnd.isAfter(request.getDailyEndTime())) {
                ResourceAvailability slot = ResourceAvailability.builder()
                        .date(current)
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .status(ResourceAvailabilityStatusEnum.AVAILABLE)
                        .resource(resource)
                        .build();

                slots.add(slot);
                slotStart = slotEnd;
                slotEnd = slotStart.plusMinutes(request.getSlotDurationMinutes());
            }

            current = current.plusDays(1);
        }

        // Save all slots in batch
        List<ResourceAvailability> saved = availabilityRepository.saveAll(slots);
        return saved.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Regenerate availability slots for a date range
     * Deletes existing slots and creates new ones
     */
    public List<ResourceAvailabilityDTO> regenerateBulkAvailabilities(Long resourceId, ResourceAvailabilityBulkRequest request) {
        // Delete existing slots for the date range
        availabilityRepository.deleteByResourceIdAndDateBetween(resourceId, request.getStartDate(), request.getEndDate());

        // Generate new slots
        return generateBulkAvailabilities(resourceId, request);
    }

    private ResourceAvailabilityDTO mapToDTO(ResourceAvailability availability) {
        return ResourceAvailabilityDTO.builder()
                .id(availability.getId())
                .resourceId(availability.getResource().getId())
                .date(availability.getDate().toString())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .status(availability.getStatus().name())
                .isBooked(Boolean.FALSE) // Will be computed by checking reservations
                .build();
    }
}
