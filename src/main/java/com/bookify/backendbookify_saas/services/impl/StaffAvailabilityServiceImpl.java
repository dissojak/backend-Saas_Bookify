package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.StaffAvailability;
import com.bookify.backendbookify_saas.repositories.StaffAvailabilityRepository;
import com.bookify.backendbookify_saas.services.StaffAvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StaffAvailabilityServiceImpl implements StaffAvailabilityService {

    private final StaffAvailabilityRepository availabilityRepository;

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
}
