package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.BusinessClientCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientResponse;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientUpdateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessClientBookingSummary;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessClient;
import com.bookify.backendbookify_saas.models.entities.Staff;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.repositories.BusinessClientRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.repositories.ServiceBookingRepository;
import com.bookify.backendbookify_saas.services.BusinessClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BusinessClientService
 * Handles CRUD operations for business-specific clients with access control
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessClientServiceImpl implements BusinessClientService {

    private final BusinessClientRepository businessClientRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final ServiceBookingRepository serviceBookingRepository;

    @Override
    @Transactional
    public BusinessClientResponse createClient(Long businessId, BusinessClientCreateRequest request, Long authenticatedUserId) {
        // Verify business exists
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Verify user has access to this business
        validateUserAccessToBusiness(authenticatedUserId, businessId);

        // Check if phone already exists for this business
        if (businessClientRepository.existsByBusinessIdAndPhone(businessId, request.getPhone())) {
            throw new RuntimeException("A client with this phone number already exists for this business");
        }

        // Create new client
        BusinessClient client = new BusinessClient();
        client.setName(request.getName());
        client.setPhone(request.getPhone());
        client.setEmail(request.getEmail());
        client.setNotes(request.getNotes());
        client.setBusiness(business);

        BusinessClient savedClient = businessClientRepository.save(client);

        return mapToResponse(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessClientResponse> getClientsByBusiness(Long businessId, Long authenticatedUserId) {
        // Verify business exists
        businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Verify user has access to this business
        validateUserAccessToBusiness(authenticatedUserId, businessId);

        List<BusinessClient> clients = businessClientRepository.findByBusinessId(businessId);

        return clients.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessClientResponse getClientById(Long businessId, Long clientId, Long authenticatedUserId) {
        // Verify user has access to this business
        validateUserAccessToBusiness(authenticatedUserId, businessId);

        BusinessClient client = businessClientRepository.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new RuntimeException("Client not found or does not belong to this business"));

        return mapToResponse(client);
    }

    @Override
    @Transactional
    public BusinessClientResponse updateClient(Long businessId, Long clientId, BusinessClientUpdateRequest request, Long authenticatedUserId) {
        // Verify user has access to this business
        validateUserAccessToBusiness(authenticatedUserId, businessId);

        BusinessClient client = businessClientRepository.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new RuntimeException("Client not found or does not belong to this business"));

        // Update fields if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            client.setName(request.getName());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            // Check if new phone already exists for this business (excluding current client)
            if (!request.getPhone().equals(client.getPhone()) &&
                businessClientRepository.existsByBusinessIdAndPhone(businessId, request.getPhone())) {
                throw new RuntimeException("A client with this phone number already exists for this business");
            }
            client.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            client.setEmail(request.getEmail().isBlank() ? null : request.getEmail());
        }
        if (request.getNotes() != null) {
            client.setNotes(request.getNotes().isBlank() ? null : request.getNotes());
        }

        BusinessClient updatedClient = businessClientRepository.save(client);

        return mapToResponse(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(Long businessId, Long clientId, Long authenticatedUserId) {
        // Verify user has access to this business
        validateUserAccessToBusiness(authenticatedUserId, businessId);

        BusinessClient client = businessClientRepository.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new RuntimeException("Client not found or does not belong to this business"));

        // Check if client has active bookings (PENDING or CONFIRMED only)
        // NO_SHOW, CANCELLED, and COMPLETED bookings are allowed and will be deleted with the client
        long activeBookingCount = serviceBookingRepository.countByBusinessClientIdAndStatusIn(
                clientId, 
                java.util.Arrays.asList(
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.PENDING,
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CONFIRMED
                )
        );
        
        if (activeBookingCount > 0) {
            throw new RuntimeException("Cannot delete client with active bookings. Client has " + activeBookingCount + " pending or confirmed booking(s).");
        }

            // Delete all non-active bookings (NO_SHOW, CANCELLED, COMPLETED) before deleting the client
            serviceBookingRepository.deleteByBusinessClientIdAndStatusIn(
                    clientId,
                    java.util.Arrays.asList(
                        com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.COMPLETED,
                        com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.NO_SHOW,
                        com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CANCELLED
                    )
            );

        businessClientRepository.delete(client);
    }

    @Override
    public BusinessClientBookingSummary getClientBookingsSummary(Long businessId, Long clientId, Long authenticatedUserId) {
        // Verify user has access to this business
        validateUserAccessToBusiness(authenticatedUserId, businessId);

        // Verify client exists
        BusinessClient client = businessClientRepository.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new RuntimeException("Client not found or does not belong to this business"));

        // Check for active bookings (PENDING or CONFIRMED)
        long activeBookingCount = serviceBookingRepository.countByBusinessClientIdAndStatusIn(
                clientId,
                java.util.Arrays.asList(
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.PENDING,
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CONFIRMED
                )
        );

        // Check for completed bookings
        long completedBookingCount = serviceBookingRepository.countByBusinessClientIdAndStatusIn(
                clientId,
                java.util.Arrays.asList(com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.COMPLETED)
        );

        // Check for all non-active bookings (NO_SHOW, CANCELLED, COMPLETED)
        long totalNonActiveBookingCount = serviceBookingRepository.countByBusinessClientIdAndStatusIn(
                clientId,
                java.util.Arrays.asList(
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.COMPLETED,
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.NO_SHOW,
                    com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.CANCELLED
                )
        );

        return new BusinessClientBookingSummary(
                activeBookingCount > 0,
                completedBookingCount > 0,
                totalNonActiveBookingCount
        );
    }

    /**
     * Validates that the authenticated user has access to the specified business
     * Access is granted to:
     * - Business owner
     * - Staff members assigned to the business
     */
    private void validateUserAccessToBusiness(Long userId, Long businessId) {
        log.info("Validating access for userId={} to businessId={}", userId, businessId);
        
        // First, check if user is staff for this business (using native query to avoid entity issues)
        Optional<Long> staffBusinessId = staffRepository.findBusinessIdById(userId);
        if (staffBusinessId.isPresent()) {
            log.info("User is Staff - checking business membership: staffBusinessId={}, requestedBusinessId={}", 
                     staffBusinessId.get(), businessId);
            if (staffBusinessId.get().equals(businessId)) {
                log.info("Access granted: User is staff for this business");
                return; // Staff member has access
            }
        }
        
        // If not staff or staff for different business, check if business owner
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User role: {}", user.getRole());

        // Check if user is the business owner (only check business if owner role)
        if (user.getRole() == RoleEnum.BUSINESS_OWNER) {
            // Use direct query to get owner's business ID to avoid lazy loading issues
            Business userBusiness = businessRepository.findByOwnerId(userId).orElse(null);
            if (userBusiness != null && userBusiness.getId().equals(businessId)) {
                log.info("Access granted: User is business owner");
                return; // Owner has access
            }
        }

        // If neither condition is met, deny access
        log.warn("Access denied for userId={} to businessId={}", userId, businessId);
        throw new RuntimeException("Access denied: You do not have permission to access this business's clients");
    }

    /**
     * Maps BusinessClient entity to response DTO
     */
    private BusinessClientResponse mapToResponse(BusinessClient client) {
        return BusinessClientResponse.builder()
                .id(client.getId())
                .name(client.getName())
                .phone(client.getPhone())
                .email(client.getEmail())
                .notes(client.getNotes())
                .businessId(client.getBusiness().getId())
                .businessName(client.getBusiness().getName())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}

