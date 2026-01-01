package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityResponse;
import com.bookify.backendbookify_saas.models.dtos.StaffHoursResponse;
import com.bookify.backendbookify_saas.models.dtos.StaffHoursUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Staff;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.services.StaffAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@RestController
@RequestMapping("/v1/staff")
@RequiredArgsConstructor
@Tag(name = "Staff - Self", description = "Endpoints for staff to manage their own settings")
@Slf4j
public class StaffController {

    private final StaffRepository staffRepository;
    private final StaffAvailabilityService staffAvailabilityService;
    private final BusinessRepository businessRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/me")
    @Operation(summary = "Get current staff member's info", description = "Returns the authenticated staff member's info including their business details")
    public ResponseEntity<?> getMyInfo(Authentication authentication) {
        if (authentication == null) {
            throw new UnauthorizedAccessException("Authentication required");
        }
        
        Long staffId;
        try {
            staffId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new UnauthorizedAccessException("Invalid authenticated user id");
        }
        
        log.info("GET /v1/staff/me called for staffId={}", staffId);
        
        // First, get the staff member basic info
        Optional<Staff> maybeStaff = staffRepository.findById(staffId);
        if (maybeStaff.isEmpty()) {
            log.warn("Staff not found for id={}", staffId);
            return ResponseEntity.ok(java.util.Map.of(
                "staffId", staffId,
                "hasBusiness", false,
                "message", "Staff record not found"
            ));
        }
        
        Staff staff = maybeStaff.get();
        var responseBuilder = java.util.HashMap.<String, Object>newHashMap(10);
        responseBuilder.put("staffId", staff.getId());
        responseBuilder.put("name", staff.getName());
        responseBuilder.put("email", staff.getEmail());
        responseBuilder.put("defaultStartTime", staff.getDefaultStartTime());
        responseBuilder.put("defaultEndTime", staff.getDefaultEndTime());
        
        // Use native query to get business_id from staff table directly
        // This avoids Hibernate's confusion between User.business and Staff.business
        Optional<Long> maybeBusinessId = staffRepository.findBusinessIdById(staffId);
        log.info("Staff {} business_id from native query: {}", staffId, maybeBusinessId.orElse(null));
        
        if (maybeBusinessId.isPresent()) {
            Long businessId = maybeBusinessId.get();
            Optional<Business> maybeBusiness = businessRepository.findById(businessId);
            if (maybeBusiness.isPresent()) {
                var b = maybeBusiness.get();
                responseBuilder.put("hasBusiness", true);
                responseBuilder.put("businessId", b.getId());
                responseBuilder.put("businessName", b.getName());
                log.info("Staff {} is linked to business {} ({})", staffId, b.getId(), b.getName());
            } else {
                responseBuilder.put("hasBusiness", false);
                log.warn("Business {} not found for staff {}", businessId, staffId);
            }
        } else {
            responseBuilder.put("hasBusiness", false);
            log.info("Staff {} has no business_id in database", staffId);
        }
        
        return ResponseEntity.ok(responseBuilder);
    }

    @PatchMapping(path = "/{staffId}/workTime", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update staff default hours , work time ", description = "Staff can update their own default start and end times")
    public ResponseEntity<StaffHoursResponse> updateMyHours(
            Authentication authentication,
            @PathVariable Long staffId,
            @RequestBody(required = false) StaffHoursUpdateRequest req
    ) {
        if (authentication == null) throw new UnauthorizedAccessException("Authentication required");
        Long actorId;
        try {
            actorId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new UnauthorizedAccessException("Invalid authenticated user id");
        }

        if (!actorId.equals(staffId)) {
            throw new UnauthorizedAccessException("Staff can only update their own hours");
        }

        // Ensure staff exists
        if (!staffRepository.existsById(staffId)) {
            throw new IllegalArgumentException("Staff not found");
        }

        if (req == null) {
            // nothing to change; return current times from DB via repository
            var maybe = staffRepository.findById(staffId);
            var s = maybe.orElseThrow(() -> new IllegalArgumentException("Staff not found"));
            return ResponseEntity.ok(StaffHoursResponse.builder()
                    .staffId(s.getId())
                    .defaultStartTime(s.getDefaultStartTime())
                    .defaultEndTime(s.getDefaultEndTime())
                    .build());
        }

        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;
        LocalTime start = null;
        LocalTime end = null;
        try {
            if (req.getDefaultStartTime() != null && !req.getDefaultStartTime().isBlank()) {
                start = LocalTime.parse(req.getDefaultStartTime(), fmt);
            }
            if (req.getDefaultEndTime() != null && !req.getDefaultEndTime().isBlank()) {
                end = LocalTime.parse(req.getDefaultEndTime(), fmt);
            }
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Time must be in HH:mm or HH:mm:ss format: " + ex.getParsedString());
        } catch (ClassCastException cce) {
            // Defensive: some bad binding might have produced unexpected types (e.g. LocalDate). Convert to clearer message.
            throw new IllegalArgumentException("Invalid JSON field types in request body (expected strings for times): " + cce.getMessage());
        }

        // Optional validation: if both provided ensure start < end
        if (start != null && end != null && !start.isBefore(end)) {
            throw new IllegalArgumentException("defaultStartTime must be before defaultEndTime");
        }

        // If both start and end are null -> nothing to update at DB level, return current values
        if (start == null && end == null) {
            return ResponseEntity.ok(StaffHoursResponse.builder()
                    .staffId(staffId)
                    .defaultStartTime(start)
                    .defaultEndTime(end)
                    .build());
        }

        // Detach any managed entities to avoid Hibernate flush/cascade during the repository update
        if (entityManager != null) {
            entityManager.clear();
        }

        // Use repository JPQL update to change only the time columns
        int updated = staffRepository.updateWorkTime(staffId, start, end);
        if (updated <= 0) {
            throw new IllegalArgumentException("Failed to update staff work time");
        }

        // Read back and return the updated times via repository (safe now)
        var saved = staffRepository.findById(staffId).orElseThrow(() -> new IllegalArgumentException("Staff not found after update"));
        return ResponseEntity.ok(StaffHoursResponse.builder()
                .staffId(saved.getId())
                .defaultStartTime(saved.getDefaultStartTime())
                .defaultEndTime(saved.getDefaultEndTime())
                .build());
    }

    // New endpoint: patch a single availability (staff updates their own availability)
    @PatchMapping(path = "/{staffId}/availabilities/{availabilityId}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Update a single staff availability", description = "Staff can edit their generated availability: change start/end/time/status")
    public ResponseEntity<?> patchAvailability(
            Authentication authentication,
            @PathVariable Long staffId,
            @PathVariable Long availabilityId,
            @RequestBody com.bookify.backendbookify_saas.models.dtos.StaffAvailabilityUpdateRequest req
    ) {
        if (authentication == null) throw new UnauthorizedAccessException("Authentication required");
        long actorId;
        try {
            actorId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new UnauthorizedAccessException("Invalid authenticated user id");
        }

        // Delegate to service to perform permission checks and update
        com.bookify.backendbookify_saas.models.entities.StaffAvailability updated = staffAvailabilityService.updateAvailabilityByStaff(actorId, staffId, availabilityId, req);

        // Map to DTO to avoid returning lazy Hibernate proxies
        StaffAvailabilityResponse resp = StaffAvailabilityResponse.builder()
                .id(updated.getId())
                .date(updated.getDate())
                .startTime(updated.getStartTime())
                .endTime(updated.getEndTime())
                .status(updated.getStatus())
                .userEdited(updated.getUserEdited())
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .staffId(updated.getStaff().getId())
                .build();

        return ResponseEntity.ok(resp);
    }

    @GetMapping(path = "/{staffId}/availabilities")
    @Operation(summary = "List staff availabilities in a date range", description = "Returns both generated and user-edited slots for a staff member")
    public ResponseEntity<List<StaffAvailabilityResponse>> listForStaff(
            @PathVariable Long staffId,
            @RequestParam(name = "from", required = false) String fromStr,
            @RequestParam(name = "to", required = false) String toStr
    ) {
        final String PLACEHOLDER = "YYYY-MM-DD";
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate from;
        java.time.LocalDate to;

        // Log raw incoming params
        log.info("listForStaff called: staffId={}, fromStr='{}', toStr='{}'", staffId, fromStr, toStr);

        // Default: if missing or placeholder, set sensible defaults
        if (fromStr == null || fromStr.isBlank() || PLACEHOLDER.equalsIgnoreCase(fromStr.trim())) {
            from = today;
            log.info("No valid 'from' provided, defaulting to {}", from);
        } else {
            try {
                from = java.time.LocalDate.parse(fromStr, java.time.format.DateTimeFormatter.ISO_DATE);
            } catch (java.time.format.DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid 'from' date format. Expected YYYY-MM-DD");
            }
        }

        if (toStr == null || toStr.isBlank() || PLACEHOLDER.equalsIgnoreCase(toStr.trim())) {
            to = from.plusDays(30);
            log.info("No valid 'to' provided, defaulting to {}", to);
        } else {
            try {
                to = java.time.LocalDate.parse(toStr, java.time.format.DateTimeFormatter.ISO_DATE);
            } catch (java.time.format.DateTimeParseException ex) {
                throw new IllegalArgumentException("Invalid 'to' date format. Expected YYYY-MM-DD");
            }
        }

        log.info("Parsed date range: from={} to={}", from, to);

        if (to.isBefore(from)) throw new IllegalArgumentException("'to' date must be on or after 'from' date");

        // Enforce a reasonable maximum range to avoid heavy queries
        long days = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1; // inclusive
        final int MAX_DAYS = 31;
        if (days > MAX_DAYS) {
            throw new IllegalArgumentException("Date range too large. Maximum allowed is " + MAX_DAYS + " days");
        }

        List<StaffAvailabilityResponse> results = staffAvailabilityService.listAvailabilitiesForStaff(staffId, from, to);
        if (results == null) results = List.of();
        return ResponseEntity.ok(results);
    }

    @GetMapping(path = "/{staffId}/calendar", produces = "application/json")
    @Operation(summary = "Get staff merged calendar starting today")
    public ResponseEntity<List<StaffAvailabilityResponse>> getCalendar(
            Authentication authentication,
            @PathVariable Long staffId
    ) {
        log.info("GET /v1/staff/{}/calendar called - authentication object: {}", staffId, authentication != null ? authentication.getName() : null);

        Long actorId = null;
        if (authentication != null) {
            try {
                actorId = Long.parseLong(authentication.getName());
            } catch (NumberFormatException ex) {
                log.warn("Invalid authenticated user id format: {}", authentication.getName());
                throw new UnauthorizedAccessException("Invalid authenticated user id");
            }
        }

        try {
            List<StaffAvailabilityResponse> calendar = staffAvailabilityService.getCalendarForStaff(actorId, staffId);
            if (calendar == null) calendar = List.of();
            log.info("Returning calendar for staffId={} (rows={}) actorId={}", staffId, calendar.size(), actorId);
            return ResponseEntity.ok(calendar);
        } catch (UnauthorizedAccessException uae) {
            log.warn("Access denied to calendar for staffId={} actorId={}: {}", staffId, actorId, uae.getMessage());
            throw uae; // rethrow so global handler returns proper error
        } catch (Exception ex) {
            log.error("Unexpected error while fetching calendar for staffId={} actorId={}", staffId, actorId, ex);
            throw ex;
        }
    }
}
