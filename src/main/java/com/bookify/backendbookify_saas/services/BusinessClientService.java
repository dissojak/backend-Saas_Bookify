package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.BusinessClientCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientResponse;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientUpdateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientBookingSummary;

import java.util.List;

/**
 * Service interface for BusinessClient operations
 */
public interface BusinessClientService {

    /**
     * Create a new business client
     */
    BusinessClientResponse createClient(Long businessId, BusinessClientCreateRequest request, Long authenticatedUserId);

    /**
     * Get all clients for a business
     */
    List<BusinessClientResponse> getClientsByBusiness(Long businessId, Long authenticatedUserId);

    /**
     * Get a specific client by ID
     */
    BusinessClientResponse getClientById(Long businessId, Long clientId, Long authenticatedUserId);

    /**
     * Update a client
     */
    BusinessClientResponse updateClient(Long businessId, Long clientId, BusinessClientUpdateRequest request, Long authenticatedUserId);

    /**
     * Delete a client
     */
    void deleteClient(Long businessId, Long clientId, Long authenticatedUserId);

    /**
     * Get booking summary information for a client (for UI deletion checks)
     */
    BusinessClientBookingSummary getClientBookingsSummary(Long businessId, Long clientId, Long authenticatedUserId);
}

