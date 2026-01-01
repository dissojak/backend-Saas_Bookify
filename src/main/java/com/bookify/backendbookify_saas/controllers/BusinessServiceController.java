package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.ServiceCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.ServiceResponse;
import com.bookify.backendbookify_saas.models.dtos.ServiceUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.ServiceRepository;
import com.bookify.backendbookify_saas.repositories.ServiceBookingRepository;
import com.bookify.backendbookify_saas.services.StaffValidationService;
import com.bookify.backendbookify_saas.services.impl.ServiceCreationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/businesses/{businessId}/services")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public - Business Services", description = "Manage services for a business")
public class BusinessServiceController {

    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final StaffValidationService staffValidationService;
    private final ServiceCreationService serviceCreationService;

    // Helper: map entity -> DTO
    private ServiceResponse toDto(Service s) {
        List<Long> staffIds = s.getStaff() == null ? List.of() : s.getStaff().stream().map(User::getId).toList();
        List<ServiceResponse.StaffInfo> staffProviders = s.getStaff() == null ? List.of() : s.getStaff().stream()
                .map(staff -> ServiceResponse.StaffInfo.builder()
                        .id(staff.getId())
                        .name(staff.getName())
                        .avatarUrl(staff.getAvatarUrl())
                        .build())
                .toList();
        return ServiceResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .durationMinutes(s.getDurationMinutes())
                .price(s.getPrice())
                .imageUrl(s.getImageUrl())
                .active(s.getActive())
                .createdById(s.getCreatedBy() != null ? s.getCreatedBy().getId() : null)
                .createdByName(s.getCreatedBy() != null ? s.getCreatedBy().getName() : null)
                .staffIds(staffIds)
                .staffProviders(staffProviders)
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }

    // Create a service (owner or staff)
    @PostMapping
    @Operation(summary = "Create service for business", description = "Owner or staff can create a service")
    public ResponseEntity<ServiceResponse> createService(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody ServiceCreateRequest req
    ) {
        Long actorId = Long.parseLong(authentication.getName());

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Determine permissions
        boolean isOwner = business.getOwner() != null && business.getOwner().getId().equals(actorId);
        boolean isStaff = staffValidationService.isStaffForBusiness(actorId, businessId);

        log.debug("createService - actorId={}, businessId={}, isOwner={}, isStaff={}, staffIds={}", actorId, businessId, isOwner, isStaff, req.getStaffIds());

        if (!isOwner && !isStaff) {
            throw new UnauthorizedAccessException("Only the owner or staff of this business can create services");
        }

        // Validate provided staff IDs before creating/persisting the Service to avoid flush interactions
        List<Long> staffIdsToAttach = req.getStaffIds() == null ? List.of() : req.getStaffIds();

        if (staffIdsToAttach.isEmpty()) {
            // If actor is owner -> allowed to create without staff (no-op attach)
            if (isOwner) {
                staffIdsToAttach = List.of();
            } else {
                // actor is not owner, must be staff -> auto-add actor if they are staff
                boolean actorIsStaff = staffValidationService.isStaffForBusiness(actorId, businessId);
                if (actorIsStaff) {
                    staffIdsToAttach = List.of(actorId);
                } else {
                    throw new IllegalArgumentException("At least one staff must be provided or the actor must be staff of the business");
                }
            }
        } else {
            // validate provided staff ids
            for (Long id : staffIdsToAttach) {
                boolean belongs = staffValidationService.isStaffForBusiness(id, businessId);
                if (!belongs) throw new IllegalArgumentException("Staff does not belong to this business: " + id);
            }
        }

        // Create the service in a new transaction (does not attach staff collection)
        Service saved = serviceCreationService.createService(businessId, req, actorId);

        // Then add the staff -> service join rows in a separate new transaction, only if there are any to add
        if (!staffIdsToAttach.isEmpty()) {
            serviceCreationService.addStaffToService(saved.getId(), staffIdsToAttach);
        }

        // Reload the service with staff and creator to avoid LazyInitializationException when mapping to DTO
        Service reloaded = serviceRepository.findByIdWithStaffAndCreator(saved.getId()).orElse(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(reloaded));
    }

    // Update a service (owner OR staff if creator)
    @PutMapping("/{serviceId}")
    @Operation(summary = "Update service", description = "Owner or staff (if creator) can update")
    public ResponseEntity<ServiceResponse> updateService(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId,
            @RequestBody ServiceUpdateRequest req
    ) {
        Long actorId = Long.parseLong(authentication.getName());
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }
        // Permission: owner of business OR creator
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isCreator = s.getCreatedBy() != null && s.getCreatedBy().getId().equals(actorId);
        if (!isOwner && !isCreator) throw new UnauthorizedAccessException("Only owner or creator can update this service");

        if (req.getName() != null) s.setName(req.getName());
        if (req.getDescription() != null) s.setDescription(req.getDescription());
        if (req.getDurationMinutes() != null) s.setDurationMinutes(req.getDurationMinutes());
        if (req.getPrice() != null) s.setPrice(req.getPrice());
        if (req.getImageUrl() != null) s.setImageUrl(req.getImageUrl());
        if (req.getActive() != null) s.setActive(req.getActive());
        if (req.getStaffIds() != null) {
            // Validate all supplied staff IDs belong to the business using a single boolean check per id
            for (Long id : req.getStaffIds()) {
                boolean belongs = staffValidationService.isStaffForBusiness(id, businessId);
                if (!belongs) throw new IllegalArgumentException("Staff does not belong to this business: " + id);
            }

            // Do NOT attach the Staff entities directly to the managed Service (avoids cascading flush issues).
            // Persist other simple Service changes first, then insert join rows in a separate transaction using ServiceCreationService.
        }

        // Apply scalar updates via JPQL partial update to avoid touching collections
        serviceRepository.updatePartial(s.getId(), req.getName(), req.getDescription(), req.getDurationMinutes(), req.getPrice(), req.getImageUrl(), req.getActive());

        // If staff IDs were supplied, ensure join rows exist using the REQUIRES_NEW addStaffToService (inserts into service_staff)
        if (req.getStaffIds() != null && !req.getStaffIds().isEmpty()) {
            serviceCreationService.addStaffToService(s.getId(), req.getStaffIds());
        }

        // Reload the service with staff and creator for the response
        Service reloaded = serviceRepository.findByIdWithStaffAndCreator(s.getId()).orElse(s);
        return ResponseEntity.ok(toDto(reloaded));
    }

    // Check if service can be safely deleted (no bookings)
    @GetMapping("/{serviceId}/delete-check")
    @Operation(summary = "Check if service can be safely deleted", description = "Returns booking counts to determine if deletion is safe")
    public ResponseEntity<?> checkServiceDeleteSafety(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        Long actorId = Long.parseLong(authentication.getName());
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }
        
        // Permission check
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isCreator = s.getCreatedBy() != null && s.getCreatedBy().getId().equals(actorId);
        if (!isOwner && !isCreator) {
            throw new UnauthorizedAccessException("Only owner or creator can check delete safety");
        }

        long totalBookings = serviceBookingRepository.countByServiceId(serviceId);
        long activeBookings = serviceBookingRepository.countActiveBookingsByServiceId(serviceId);
        boolean safeToDelete = totalBookings == 0;
        
        return ResponseEntity.ok(java.util.Map.of(
            "serviceId", serviceId,
            "serviceName", s.getName(),
            "totalBookings", totalBookings,
            "activeBookings", activeBookings,
            "safeToDelete", safeToDelete,
            "message", safeToDelete 
                ? "This service has no bookings and can be safely deleted."
                : activeBookings > 0 
                    ? "Warning: This service has " + activeBookings + " active booking(s). Deleting may cause issues for clients."
                    : "This service has " + totalBookings + " historical booking(s). These records will become orphaned."
        ));
    }

    // Deactivate service (soft delete - hides from booking but preserves data)
    @PatchMapping("/{serviceId}/deactivate")
    @Operation(summary = "Deactivate service", description = "Owner or creator can deactivate. Service will be hidden from bookings but data is preserved.")
    public ResponseEntity<java.util.Map<String, Object>> deactivateService(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        Long actorId = Long.parseLong(authentication.getName());
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isCreator = s.getCreatedBy() != null && s.getCreatedBy().getId().equals(actorId);
        if (!isOwner && !isCreator) throw new UnauthorizedAccessException("Only owner or creator can deactivate this service");

        s.setActive(false);
        serviceRepository.save(s);
        
        return ResponseEntity.ok(java.util.Map.of(
            "serviceId", serviceId,
            "serviceName", s.getName(),
            "action", "deactivated",
            "message", "Service deactivated. It will no longer appear for new bookings but all data is preserved."
        ));
    }

    // Reactivate service (restore from soft delete)
    @PatchMapping("/{serviceId}/activate")
    @Operation(summary = "Reactivate service", description = "Owner or creator can reactivate a deactivated service.")
    public ResponseEntity<java.util.Map<String, Object>> reactivateService(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        Long actorId = Long.parseLong(authentication.getName());
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isCreator = s.getCreatedBy() != null && s.getCreatedBy().getId().equals(actorId);
        if (!isOwner && !isCreator) throw new UnauthorizedAccessException("Only owner or creator can reactivate this service");

        s.setActive(true);
        serviceRepository.save(s);
        
        return ResponseEntity.ok(java.util.Map.of(
            "serviceId", serviceId,
            "serviceName", s.getName(),
            "action", "activated",
            "message", "Service reactivated. It will now appear for new bookings."
        ));
    }

    // Delete service (owner OR creator) - permanent deletion
    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Delete service", description = "Owner or creator can delete. This is permanent.")
    public ResponseEntity<Void> deleteService(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        Long actorId = Long.parseLong(authentication.getName());
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isCreator = s.getCreatedBy() != null && s.getCreatedBy().getId().equals(actorId);
        if (!isOwner && !isCreator) throw new UnauthorizedAccessException("Only owner or creator can delete this service");

        serviceRepository.delete(s);
        return ResponseEntity.noContent().build();
    }

    // Get service details
    @GetMapping("/{serviceId}")
    @Operation(summary = "Get service details")
    public ResponseEntity<ServiceResponse> getService(
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        // Use findByIdWithStaffAndCreator to eagerly fetch staff and creator to avoid LazyInitializationException
        Service s = serviceRepository.findByIdWithStaffAndCreator(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(s));
    }

    // List services offered by business
    @GetMapping
    @Operation(summary = "List services for business", description = "By default returns only active services. Use includeInactive=true to get all services.")
    public ResponseEntity<List<ServiceResponse>> listServices(
            @PathVariable Long businessId,
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive
    ) {
        List<Service> list;
        if (includeInactive) {
            list = serviceRepository.findByBusinessId(businessId);
        } else {
            list = serviceRepository.findByBusinessIdAndActiveTrue(businessId);
        }
        List<ServiceResponse> dto = list.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    // Allow a staff member to link themselves to a service
    @PostMapping("/{serviceId}/staff/me")
    @Operation(summary = "Link authenticated staff to service", description = "Only a staff of the business can link themselves to a service")
    public ResponseEntity<ServiceResponse> linkSelfToService(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        Long actorId = Long.parseLong(authentication.getName());

        // Load service (do not modify scalar fields here)
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }

        // Permission: allow business owner OR any staff of the business
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isStaff = staffValidationService.isStaffForBusiness(actorId, businessId);
        if (!isOwner && !isStaff) throw new UnauthorizedAccessException("Only owner or staff of this business can modify service staff links");

        // This endpoint does not accept any scalar changes (no request body). We'll add the authenticated user only.
        // Build the list containing only the authenticated user's id (this endpoint is join-only)
        List<Long> staffIdsToAdd = List.of(actorId);

        // Validate actor is staff OR owner (if owner isn't staff we still allow linking the owner)
        if (!isOwner && !staffValidationService.isStaffForBusiness(actorId, businessId)) {
            throw new IllegalArgumentException("Authenticated user is not a staff of this business");
        }

        // Insert join row(s) using the existing robust helper
        serviceCreationService.addStaffToService(serviceId, staffIdsToAdd);

        // Reload and return updated service with staff
        Service reloaded = serviceRepository.findByIdWithStaffAndCreator(serviceId).orElse(s);
        return ResponseEntity.ok(toDto(reloaded));
    }

    // Allow a staff member to unlink themselves from a service
    @DeleteMapping("/{serviceId}/staff/me")
    @Operation(summary = "Unlink authenticated staff from service", description = "Staff can remove themselves from a service they're linked to")
    public ResponseEntity<ServiceResponse> unlinkSelfFromService(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long serviceId
    ) {
        Long actorId = Long.parseLong(authentication.getName());

        // Load service
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            throw new IllegalArgumentException("Service does not belong to business");
        }

        // Permission: allow business owner OR any staff of the business to unlink themselves
        boolean isOwner = s.getBusiness().getOwner() != null && s.getBusiness().getOwner().getId().equals(actorId);
        boolean isStaff = staffValidationService.isStaffForBusiness(actorId, businessId);
        if (!isOwner && !isStaff) {
            throw new UnauthorizedAccessException("Only owner or staff of this business can modify service staff links");
        }

        // Remove the staff from the service
        serviceCreationService.removeStaffFromService(serviceId, actorId);

        // Reload and return updated service with staff
        Service reloaded = serviceRepository.findByIdWithStaffAndCreator(serviceId).orElse(s);
        return ResponseEntity.ok(toDto(reloaded));
    }
}
