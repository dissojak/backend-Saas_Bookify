package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.ServiceCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.ServiceResponse;
import com.bookify.backendbookify_saas.models.dtos.ServiceUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.Staff;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.ServiceRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
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
    private final StaffRepository staffRepository;
    private final StaffValidationService staffValidationService;
    private final ServiceCreationService serviceCreationService;

    // Helper: map entity -> DTO
    private ServiceResponse toDto(Service s) {
        List<Long> staffIds = s.getStaff() == null ? List.of() : s.getStaff().stream().map(User::getId).collect(Collectors.toList());
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
            List<Staff> staffList = req.getStaffIds().stream().map(id -> {
                Staff st = staffRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Staff user not found: " + id));
                if (st.getBusiness() == null || !st.getBusiness().getId().equals(businessId))
                    throw new IllegalArgumentException("Staff does not belong to this business: " + id);
                return st;
            }).collect(Collectors.toList());
            s.setStaff(staffList);
        }

        Service saved = serviceRepository.save(s);
        return ResponseEntity.ok(toDto(saved));
    }

    // Delete service (owner OR creator)
    @DeleteMapping("/{serviceId}")
    @Operation(summary = "Delete service", description = "Owner or creator can delete")
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
        Service s = serviceRepository.findById(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
        if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDto(s));
    }

    // List services offered by business
    @GetMapping
    @Operation(summary = "List services for business")
    public ResponseEntity<List<ServiceResponse>> listServices(@PathVariable Long businessId) {
        List<Service> list = serviceRepository.findByBusinessIdAndActiveTrue(businessId);
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
        try {
            Long actorId = Long.parseLong(authentication.getName());

            // Verify the actor is staff for this business
            boolean isStaff = staffValidationService.isStaffForBusiness(actorId, businessId);
            if (!isStaff) {
                throw new UnauthorizedAccessException("Only staff of this business can link themselves to a service");
            }

            // Load service with staff to check ownership and existing linkage
            Service s = serviceRepository.findByIdWithStaffAndCreator(serviceId).orElseThrow(() -> new IllegalArgumentException("Service not found"));
            if (s.getBusiness() == null || !s.getBusiness().getId().equals(businessId)) {
                throw new IllegalArgumentException("Service does not belong to business");
            }

            boolean alreadyLinked = s.getStaff() != null && s.getStaff().stream().anyMatch(u -> u.getId().equals(actorId));
            if (alreadyLinked) {
                // Already linked — nothing to do
                return ResponseEntity.noContent().build();
            }

            // Use the ServiceCreationService which runs the insert in a new transaction
            serviceCreationService.addStaffToService(serviceId, List.of(actorId));

            // Reload the service with staff and creator for response
            Service reloaded = serviceRepository.findByIdWithStaffAndCreator(serviceId).orElse(s);
            return ResponseEntity.ok(toDto(reloaded));
        } catch (Exception ex) {
            // Log full exception for debugging and return an internal error message to client
            log.error("linkSelfToService failed for businessId={}, serviceId={}, actorId={}", businessId, serviceId, (authentication != null ? authentication.getName() : "null"), ex);
            // Build a simple error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceResponse.builder()
                            .id(null)
                            .name("Internal error")
                            .description(ex.getMessage())
                            .build());
        }
    }
}
