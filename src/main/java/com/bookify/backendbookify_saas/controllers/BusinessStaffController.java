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
import com.bookify.backendbookify_saas.services.StaffAvailabilityGeneratorService;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private final StaffAvailabilityGeneratorService availabilityGeneratorService;

    @PersistenceContext
    private EntityManager em;

    // Owner adds a staff by email; upgrades user -> staff and links to business
    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Add staff by email with optional work hours (owner only)")
    public ResponseEntity<?> addStaffByEmail(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody java.util.Map<String, String> body
    ) {
        String email = body.get("email");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email is required");

        // Parse optional work hours
        String startTimeStr = body.get("startTime");
        String endTimeStr = body.get("endTime");
        
        // Validate: if one time is provided, both must be provided
        if ((startTimeStr != null && !startTimeStr.isBlank()) != (endTimeStr != null && !endTimeStr.isBlank())) {
            throw new IllegalArgumentException("Both startTime and endTime must be provided together, or neither");
        }
        
        LocalTime startTime = null;
        LocalTime endTime = null;
        
        if (startTimeStr != null && !startTimeStr.isBlank()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_TIME;
                startTime = LocalTime.parse(startTimeStr, fmt);
                endTime = LocalTime.parse(endTimeStr, fmt);
                
                if (!startTime.isBefore(endTime)) {
                    throw new IllegalArgumentException("startTime must be before endTime");
                }
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Time must be in HH:mm or HH:mm:ss format");
            }
        }

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
            if (startTime != null && endTime != null) {
                // Create with work hours and auto-generate availabilities
                staffService.createStaffAndSetRoleWithWorkHours(userId, businessId, startTime, endTime);
            } else {
                // Create without work hours (staff will need to set them later)
                staffService.createStaffAndSetRole(userId, businessId);
            }
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
                "staffId", userId,
                "workHoursSet", startTime != null
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
            try {
                em.createNativeQuery("DELETE FROM service_staff WHERE service_id = :serviceId AND staff_id = :staffId")
                        .setParameter("serviceId", svc.getId())
                        .setParameter("staffId", staffId)
                        .executeUpdate();
            } catch (Exception ex) {
                log.warn("Failed to delete join row for serviceId={} staffId={} via native query -> {}", svc.getId(), staffId, ex.toString());
            }
        }

        // Delete staff availabilities first (they reference staff)
        try {
            int deletedAvailabilities = em.createNativeQuery("DELETE FROM staff_availabilities WHERE staff_id = :staffId")
                    .setParameter("staffId", staffId)
                    .executeUpdate();
            log.info("Deleted {} staff_availabilities records for staffId={}", deletedAvailabilities, staffId);
        } catch (Exception ex) {
            log.warn("Failed to delete staff_availabilities for staffId={}: {}", staffId, ex.getMessage());
        }

        // Set staff_id to NULL in bookings (keep historical booking records)
        try {
            int updatedBookings = em.createNativeQuery("UPDATE service_bookings SET staff_id = NULL WHERE staff_id = :staffId")
                    .setParameter("staffId", staffId)
                    .executeUpdate();
            log.info("Unlinked {} bookings from staffId={}", updatedBookings, staffId);
        } catch (Exception ex) {
            log.warn("Failed to unlink bookings from staffId={}: {}", staffId, ex.getMessage());
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

    // Business Owner registers themselves as staff in their own business with optional work hours
    @PostMapping("/self")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Transactional
    @Operation(summary = "Business owner adds themselves as staff with optional work hours")
    public ResponseEntity<?> addSelfAsStaff(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody(required = false) java.util.Map<String, String> body
    ) {
        Long actorId = Long.parseLong(authentication.getName());

        // Parse optional work hours from request body
        String startTimeStr = body != null ? body.get("startTime") : null;
        String endTimeStr = body != null ? body.get("endTime") : null;
        
        LocalTime startTime = null;
        LocalTime endTime = null;
        
        if (startTimeStr != null && !startTimeStr.isBlank() && endTimeStr != null && !endTimeStr.isBlank()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_TIME;
                startTime = LocalTime.parse(startTimeStr, fmt);
                endTime = LocalTime.parse(endTimeStr, fmt);
                
                if (!startTime.isBefore(endTime)) {
                    throw new IllegalArgumentException("startTime must be before endTime");
                }
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Time must be in HH:mm or HH:mm:ss format");
            }
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Verify this BO owns the business
        if (business.getOwner() == null || !business.getOwner().getId().equals(actorId)) {
            throw new UnauthorizedAccessException("Only the business owner can perform this action");
        }

        // Check if already staff
        Optional<Long> maybeBusinessId = staffRepository.findBusinessIdById(actorId);
        if (maybeBusinessId.isPresent()) {
            Long existingBusinessId = maybeBusinessId.get();
            if (existingBusinessId.equals(businessId)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(java.util.Map.of("message", "You are already working as staff in this business"));
            } else {
                throw new UserAlreadyStaffException("You are already staff for another business");
            }
        }

        // Create staff row and optionally generate availabilities
        try {
            if (startTime != null && endTime != null) {
                // Insert staff row with work hours
                String startStr = startTime.toString();
                String endStr = endTime.toString();
                staffRepository.insertStaffRowWithWorkHours(actorId, businessId, startStr, endStr);
                
                // Auto-generate availabilities for next month
                try {
                    int generated = availabilityGeneratorService.generateForSingleStaff(actorId, startTime, endTime);
                    log.info("Auto-generated {} availabilities for BO-as-staff {}", generated, actorId);
                } catch (Exception genEx) {
                    log.error("Failed to auto-generate availabilities for BO {}: {}", actorId, genEx.getMessage());
                    // Don't fail the staff creation if availability generation fails
                }
            } else {
                // Insert without work hours - will need to set later
                staffRepository.insertStaffRow(actorId, businessId);
            }

            log.info("Business owner {} added themselves as staff in business {}", actorId, businessId);

            return ResponseEntity.status(HttpStatus.CREATED).body(java.util.Map.of(
                    "message", "You are now working as staff",
                    "staffId", actorId,
                    "workHoursSet", startTime != null,
                    "needsServiceAssignment", true
            ));
        } catch (Exception ex) {
            log.error("Failed to add BO as staff: {}", ex.getMessage());
            // Check if it was a duplicate insert that slipped through
            Optional<Long> check = staffRepository.findBusinessIdById(actorId);
            if (check.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(java.util.Map.of("message", "You are already working as staff in this business"));
            }
            throw ex;
        }
    }

    // Business Owner removes themselves from staff in their business
    @DeleteMapping("/self")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Transactional
    @Operation(summary = "Business owner removes themselves from staff")
    public ResponseEntity<?> removeSelfAsStaff(
            Authentication authentication,
            @PathVariable Long businessId
    ) {
        Long actorId = Long.parseLong(authentication.getName());

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Verify this BO owns the business
        if (business.getOwner() == null || !business.getOwner().getId().equals(actorId)) {
            throw new UnauthorizedAccessException("Only the business owner can perform this action");
        }

        // Verify they are actually staff
        Optional<Integer> staffBusinessIntOpt = staffRepository.findBusinessIdIntById(actorId);
        if (staffBusinessIntOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("message", "You are not currently working as staff"));
        }

        long foundBusinessId = staffBusinessIntOpt.get().longValue();
        if (foundBusinessId != businessId.longValue()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("message", "Staff record found for a different business"));
        }

        try {
            // Remove from all services
            List<Service> services = serviceRepository.findByBusinessIdAndActiveTrue(businessId);
            for (Service svc : services) {
                try {
                    em.createNativeQuery("DELETE FROM service_staff WHERE service_id = :serviceId AND staff_id = :staffId")
                            .setParameter("serviceId", svc.getId())
                            .setParameter("staffId", actorId)
                            .executeUpdate();
                } catch (Exception ex) {
                    log.warn("Failed to delete join row for serviceId={} staffId={}: {}", svc.getId(), actorId, ex.toString());
                }
            }

            // Delete availabilities
            try {
                em.createNativeQuery("DELETE FROM staff_availabilities WHERE staff_id = :staffId")
                        .setParameter("staffId", actorId)
                        .executeUpdate();
            } catch (Exception ex) {
                log.warn("Failed to delete availabilities for staffId={}: {}", actorId, ex.getMessage());
            }

            // Set staff_id to NULL in bookings (keep history)
            try {
                em.createNativeQuery("UPDATE service_bookings SET staff_id = NULL WHERE staff_id = :staffId")
                        .setParameter("staffId", actorId)
                        .executeUpdate();
            } catch (Exception ex) {
                log.warn("Failed to unlink bookings from staffId={}: {}", actorId, ex.getMessage());
            }

            // Delete staff row (but user role stays BUSINESS_OWNER)
            em.createNativeQuery("DELETE FROM staff WHERE id = ?")
                    .setParameter(1, actorId)
                    .executeUpdate();

            log.info("Business owner {} removed themselves from staff", actorId);

            return ResponseEntity.ok(java.util.Map.of("message", "You have stopped working as staff"));
        } catch (Exception ex) {
            log.error("Failed to remove BO from staff: {}", ex.getMessage());
            throw ex;
        }
    }
}
