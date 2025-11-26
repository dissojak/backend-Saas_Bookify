package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.ServiceResponse;

import java.util.List;

/**
 * Service contract for public staff-related read operations.
 * Implementations should provide read-only operations used by controllers.
 */
public interface StaffPublicService {

    /**
     * List active services linked to the given staff id.
     * @param staffId staff identifier
     * @return list of ServiceResponse DTOs
     */
    List<ServiceResponse> listServicesForStaff(Long staffId);
}
