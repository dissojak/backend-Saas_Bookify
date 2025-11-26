package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;

import java.util.List;

/**
 * Service contract for listing staff members for a business.
 */
public interface StaffListingService {
    /**
     * Return a list of staff profiles for the given business id.
     * @param businessId the business id
     * @return list of UserProfileResponse DTOs (empty list if none)
     */
    List<UserProfileResponse> listStaffForBusiness(Long businessId);
}

