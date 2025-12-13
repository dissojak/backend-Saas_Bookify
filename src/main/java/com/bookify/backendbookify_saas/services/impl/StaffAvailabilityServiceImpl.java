package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityUpdateRequest;
import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityResponse;
import com.bookify.backendbookify_saas.models.entities.StaffAvailability;
import com.bookify.backendbookify_saas.repositories.StaffAvailabilityRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.services.StaffAvailabilityService;
import com.bookify.backendbookify_saas.services.StaffValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffAvailabilityServiceImpl implements StaffAvailabilityService {

    private final StaffAvailabilityRepository availabilityRepository;
    private final StaffRepository staffRepository;
    private final BusinessRepository businessRepository;
    private final StaffValidationService staffValidationService;

    /**
     * Update a single availability. Only the staff member themself can update their own availability.
     * If other staff/owner needs to update, a different method with broader permissions should be added.
     */
    @Override
    @Transactional
    public StaffAvailability updateAvailabilityByStaff(Long actorStaffId, Long staffId, Long availabilityId, StaffAvailabilityUpdateRequest req) {
        if (!actorStaffId.equals(staffId)) {
            throw new UnauthorizedAccessException("Staff can only update their own availabilities");
        }

        StaffAvailability avail = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        // Ensure the availability belongs to the staff
        if (!avail.getStaff().getId().equals(staffId)) {
            throw new UnauthorizedAccessException("Availability does not belong to the authenticated staff");
        }

        // Update permitted fields only
        if (req.getStartTime() != null) avail.setStartTime(req.getStartTime());
        if (req.getEndTime() != null) avail.setEndTime(req.getEndTime());
        if (req.getStatus() != null) avail.setStatus(req.getStatus());

        // Mark as user edited
        avail.setUserEdited(true);
        avail.setUpdatedAt(LocalDateTime.now());

        return availabilityRepository.save(avail);
    }

    // New method implementation
    @Override
    @Transactional(readOnly = true)
    public List<StaffAvailabilityResponse> listAvailabilitiesForStaff(Long staffId, java.time.LocalDate from, java.time.LocalDate to) {
        if (from == null || to == null) throw new IllegalArgumentException("from and to dates are required");
        if (to.isBefore(from)) throw new IllegalArgumentException("'to' date must be after or equal to 'from' date");

        List<StaffAvailability> rows = availabilityRepository.findByStaffIdAndDateRange(staffId, from, to);
        // Defensive: ensure repository didn't return out-of-range rows (some DB/driver oddities can happen)
        java.util.List<StaffAvailability> filtered = rows.stream()
                .filter(sa -> sa.getDate() != null && (!sa.getDate().isBefore(from)) && (!sa.getDate().isAfter(to)))
                .sorted(java.util.Comparator.comparing(StaffAvailability::getDate))
                .toList();

        // Log for debugging: how many rows fetched vs returned
        if (filtered.size() != rows.size()) {
            System.out.println("[StaffAvailabilityService] repository returned " + rows.size() + " rows, filtered to " + filtered.size() + " for range " + from + ".." + to);
        }

        java.util.List<StaffAvailabilityResponse> res = new java.util.ArrayList<>();
        for (StaffAvailability sa : filtered) {
            res.add(StaffAvailabilityResponse.builder()
                    .id(sa.getId())
                    .date(sa.getDate())
                    .startTime(sa.getStartTime())
                    .endTime(sa.getEndTime())
                    .status(sa.getStatus())
                    .userEdited(sa.getUserEdited())
                    .createdAt(sa.getCreatedAt())
                    .updatedAt(sa.getUpdatedAt())
                    .staffId(sa.getStaff().getId())
                    .build());
        }
        return res;
    }

    /**
     * Return merged availability calendar for a staff starting from today until the maximum availability date recorded.
     * Performs permission checks to ensure the actor belongs to the same business (owner or staff) or is the staff themself.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffAvailabilityResponse> getCalendarForStaff(Long actorId, Long staffId) {
        // Ensure target staff exists and obtain their business id
        var maybeBusinessId = staffRepository.findBusinessIdById(staffId);
        if (maybeBusinessId.isEmpty()) {
            throw new IllegalArgumentException("Staff not found or not linked to a business");
        }
        Long staffBusinessId = maybeBusinessId.get();

        // If actorId is provided, perform authorization checks; if null, allow public read-only access
        if (actorId != null) {
            if (!actorId.equals(staffId)) {
                boolean actorIsStaff = false;
                try {
                    actorIsStaff = staffValidationService.isStaffForBusiness(actorId, staffBusinessId);
                } catch (Exception ignored) {}

                boolean actorIsOwner = businessRepository.findByOwnerId(actorId).map(b -> b.getId().equals(staffBusinessId)).orElse(false);

                if (!actorIsStaff && !actorIsOwner) {
                    throw new UnauthorizedAccessException("Authenticated user is not authorized to view this staff's calendar");
                }
            }
        }

        java.time.LocalDate from = java.time.LocalDate.now();
        java.time.LocalDate maxDate = availabilityRepository.findMaxDateByStaffId(staffId);
        if (maxDate == null) {
            return List.of();
        }

        if (maxDate.isBefore(from)) {
            // No future availability starting today
            return List.of();
        }

        List<StaffAvailability> rows = availabilityRepository.findByStaffIdAndDateRange(staffId, from, maxDate);
        java.util.List<StaffAvailabilityResponse> res = new java.util.ArrayList<>();
        for (StaffAvailability sa : rows.stream().sorted(java.util.Comparator.comparing(StaffAvailability::getDate)).toList()) {
            res.add(StaffAvailabilityResponse.builder()
                    .id(sa.getId())
                    .date(sa.getDate())
                    .startTime(sa.getStartTime())
                    .endTime(sa.getEndTime())
                    .status(sa.getStatus())
                    .userEdited(sa.getUserEdited())
                    .createdAt(sa.getCreatedAt())
                    .updatedAt(sa.getUpdatedAt())
                    .staffId(sa.getStaff().getId())
                    .build());
        }

        return res;
    }
}
