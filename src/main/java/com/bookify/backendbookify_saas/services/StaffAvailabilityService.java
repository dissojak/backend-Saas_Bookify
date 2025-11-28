package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityUpdateRequest;
import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityResponse;
import com.bookify.backendbookify_saas.models.entities.StaffAvailability;

import java.time.LocalDate;
import java.util.List;

public interface StaffAvailabilityService {
    StaffAvailability updateAvailabilityByStaff(Long actorStaffId, Long staffId, Long availabilityId, StaffAvailabilityUpdateRequest req);

    /**
     * List all availabilities for a staff within a date range (inclusive)
     */
    List<StaffAvailabilityResponse> listAvailabilitiesForStaff(Long staffId, LocalDate from, LocalDate to);
}
