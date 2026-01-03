package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.BusinessClientCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientResponse;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientUpdateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientBookingSummary;
import com.bookify.backendbookify_saas.services.BusinessClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing business-specific clients
 * Only accessible by business owners and staff members of the business
 */
@RestController
@RequestMapping("/v1/business/{businessId}/clients")
@RequiredArgsConstructor
@Tag(name = "Business Clients", description = "Manage clients specific to a business")
public class BusinessClientController {

    private final BusinessClientService businessClientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")
    @Operation(summary = "Create a new client for the business", description = "Only accessible by business owner or staff")
    public ResponseEntity<BusinessClientResponse> createClient(
            @PathVariable Long businessId,
            @Valid @RequestBody BusinessClientCreateRequest request,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        BusinessClientResponse response = businessClientService.createClient(businessId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")
    @Operation(summary = "Get all clients for the business", description = "Only accessible by business owner or staff")
    public ResponseEntity<List<BusinessClientResponse>> getAllClients(
            @PathVariable Long businessId,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<BusinessClientResponse> clients = businessClientService.getClientsByBusiness(businessId, userId);
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")
    @Operation(summary = "Get a specific client by ID", description = "Only accessible by business owner or staff")
    public ResponseEntity<BusinessClientResponse> getClientById(
            @PathVariable Long businessId,
            @PathVariable Long clientId,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        BusinessClientResponse response = businessClientService.getClientById(businessId, clientId, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")
    @Operation(summary = "Update a client", description = "Only accessible by business owner or staff")
    public ResponseEntity<BusinessClientResponse> updateClient(
            @PathVariable Long businessId,
            @PathVariable Long clientId,
            @Valid @RequestBody BusinessClientUpdateRequest request,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        BusinessClientResponse response = businessClientService.updateClient(businessId, clientId, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")
    @Operation(summary = "Delete a client", description = "Only accessible by business owner or staff")
    public ResponseEntity<Void> deleteClient(
            @PathVariable Long businessId,
            @PathVariable Long clientId,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        businessClientService.deleteClient(businessId, clientId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clientId}/bookings/summary")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'STAFF')")
    @Operation(summary = "Get booking summary for a client", description = "Returns counts and status info about client's bookings")
    public ResponseEntity<BusinessClientBookingSummary> getClientBookingsSummary(
            @PathVariable Long businessId,
            @PathVariable Long clientId,
            Authentication authentication
    ) {
        Long userId = getUserIdFromAuthentication(authentication);
        BusinessClientBookingSummary summary = businessClientService.getClientBookingsSummary(businessId, clientId, userId);
        return ResponseEntity.ok(summary);
    }

    /**
     * Extract user ID from authentication
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Authentication missing");
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid authenticated user id");
        }
    }
}
