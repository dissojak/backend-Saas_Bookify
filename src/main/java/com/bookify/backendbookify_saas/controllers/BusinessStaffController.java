package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.exceptions.UserAlreadyStaffException;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.Staff;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.ServiceRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.services.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/businesses/{businessId}/staff")
@RequiredArgsConstructor
@Tag(name = "Owner - Staff", description = "Manage staff membership for a business")
@Slf4j
public class BusinessStaffController {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;
    private final StaffService staffService;

    @PersistenceContext
    private EntityManager em;

    // Owner adds a staff by email; upgrades user -> staff and links to business
    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Add staff by email (owner only)")
    public ResponseEntity<?> addStaffByEmail(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody java.util.Map<String, String> body
    ) {
        String email = body.get("email");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");

        Long actorId = Long.parseLong(authentication.getName());

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (business.getOwner() == null || !business.getOwner().getId().equals(actorId)) {
            throw new UnauthorizedAccessException("Only the business owner can add staff");
        }

        Optional<Long> maybeUserId = userRepository.findIdByEmail(email);
        Long userId = maybeUserId.orElseThrow(() -> new IllegalArgumentException("User not found for email: " + email));

        // If already staff
        Optional<Long> maybeBusinessId = staffRepository.findBusinessIdById(userId);
        if (maybeBusinessId.isPresent()) {
            Long existingBusinessId = maybeBusinessId.get();
            if (!existingBusinessId.equals(businessId)) {
                throw new UserAlreadyStaffException("User is already staff for another business");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("message", "User is already staff for this business"));
        }

        // Update role and create staff row in a separate transaction to avoid Hibernate flush/cascade issues
        try {
            staffService.createStaffAndSetRole(userId, businessId);
        } catch (Exception ex) {
            // Defensive: if any exception happened during the isolated creation, check DB state —
            // if the staff row exists, return 409 instead of propagating a 500.
            Optional<Long> check = staffRepository.findBusinessIdById(userId);
            if (check.isPresent()) {
                Long existingBusinessId = check.get();
                if (!existingBusinessId.equals(businessId)) {
                    throw new UserAlreadyStaffException("User is already staff for another business");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("message", "User is already staff for this business"));
            }
            // Not a race / duplicate case — rethrow so GlobalExceptionHandler maps it to 500
            throw ex;
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                "message", "User upgraded to staff and linked to business",
                "staffId", userId
        ));
    }

    // Staff resigns OR owner kicks staff
    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER','STAFF')")
    @Transactional
    @Operation(summary = "Remove staff from business (owner kick or staff resign)")
    public ResponseEntity<?> removeStaff(
            Authentication authentication,
            @PathVariable Long businessId,
            @PathVariable Long staffId
    ) {
        Long actorId = Long.parseLong(authentication.getName());

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Verify staff exists and belongs to this business using lightweight query
        // Use the integer-returning query to avoid native SQL numeric mapping issues
        Optional<Integer> staffBusinessIntOpt = staffRepository.findBusinessIdIntById(staffId);
        if (staffBusinessIntOpt.isEmpty()) {
            log.debug("deleteStaff: no staff row found for staffId={} in DB", staffId);
            throw new IllegalArgumentException("Staff not found for this business (no staff row)");
        }
        long foundBusinessId = staffBusinessIntOpt.get().longValue();
        if (foundBusinessId != businessId.longValue()) {
            log.debug("deleteStaff: staffId={} belongs to businessId={}, request businessId={}", staffId, foundBusinessId, businessId);
            throw new IllegalArgumentException("Staff not found for this business (found businessId=" + foundBusinessId + ")");
        }

        boolean actorIsOwner = business.getOwner() != null && business.getOwner().getId().equals(actorId);
        boolean actorIsSelf = actorId.equals(staffId);

        if (!actorIsOwner && !actorIsSelf) {
            throw new UnauthorizedAccessException("Only the owner or the staff member themselves can remove the staff role");
        }

        // Remove staff from all services of this business
        List<Service> services = serviceRepository.findByBusinessIdAndActiveTrue(businessId);
        for (Service svc : services) {
            if (svc.getStaff() != null) {
                boolean changed = svc.getStaff().removeIf(s -> s.getId().equals(staffId));
                if (changed) serviceRepository.save(svc);
            }
        }

        // delete staff row
        em.createNativeQuery("DELETE FROM staff WHERE id = ?")
                .setParameter(1, staffId)
                .executeUpdate();

        // revert user role to CLIENT
        User user = userRepository.findById(staffId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRole(RoleEnum.CLIENT);
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }
}
