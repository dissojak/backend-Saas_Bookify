package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.StaffAvailability;

public interface StaffAvailabilityService {
    StaffAvailability updateAvailabilityByStaff(Long actorStaffId, Long staffId, Long availabilityId, StaffAvailabilityUpdateRequest req);
}

