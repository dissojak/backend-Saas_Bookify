package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.BusinessCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessEvaluationResponse;
import com.bookify.backendbookify_saas.models.dtos.BusinessResponse;
import com.bookify.backendbookify_saas.models.dtos.BusinessUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessEvaluation;
import com.bookify.backendbookify_saas.models.entities.BusinessImage;
import com.bookify.backendbookify_saas.models.entities.Category;
import com.bookify.backendbookify_saas.repositories.BusinessEvaluationRepository;
import com.bookify.backendbookify_saas.repositories.BusinessImageRepository;
import com.bookify.backendbookify_saas.services.BusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/owner/businesses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Owner - Business", description = "Gestion des entreprises par le propriétaire")
public class BusinessOwnerController {

    private final BusinessService businessService;
    private final BusinessEvaluationRepository evaluationRepository;
    private final BusinessImageRepository imageRepository;
    private final com.bookify.backendbookify_saas.services.WeekendDaySyncService weekendDaySyncService;

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Créer un nouveau business", description = "Accessible uniquement aux BusinessOwner vérifiés")
    public ResponseEntity<BusinessResponse> createBusiness(
            Authentication authentication,
            @Valid @RequestBody BusinessCreateRequest request
    ) {
        // authentication.getName() contains the user id (as string) from the JWT subject
        Long ownerId;
        try {
            ownerId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid authenticated user id");
        }

        Business created = businessService.createBusinessForOwner(
                ownerId,
                request.getName(),
                request.getLocation(),
                request.getPhone(),
                request.getEmail(),
                request.getCategoryId(),
                request.getDescription()
        );

        BusinessEvaluationResponse evalDto = mapLatestEvaluation(created);
        String firstImage = getFirstImageUrl(created.getId());

        BusinessResponse response = BusinessResponse.builder()
                .id(created.getId())
                .name(created.getName())
                .location(created.getLocation())
                .phone(created.getPhone())
                .email(created.getEmail())
                .status(created.getStatus())
                .categoryId(created.getCategory() != null ? created.getCategory().getId() : null)
                .categoryName(created.getCategory() != null ? created.getCategory().getName() : null)
                .ownerId(created.getOwner() != null ? created.getOwner().getId() : null)
                .description(created.getDescription())
                .evaluation(evalDto)
                .firstImageUrl(firstImage)
                .weekendDay(created.getWeekendDay())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Get business owned by authenticated user", description = "Retrieve business for current owner")
    public ResponseEntity<BusinessResponse> getMyBusiness(Authentication authentication) {
        Long ownerId = Long.parseLong(authentication.getName());

        var business = businessService.getBusinessByOwnerId(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("No business found for this owner"));

        BusinessEvaluationResponse evalDto = mapLatestEvaluation(business);
        String firstImage = getFirstImageUrl(business.getId());

        BusinessResponse response = BusinessResponse.builder()
                .id(business.getId())
                .name(business.getName())
                .location(business.getLocation())
                .phone(business.getPhone())
                .email(business.getEmail())
                .status(business.getStatus())
                .categoryId(business.getCategory() != null ? business.getCategory().getId() : null)
                .categoryName(business.getCategory() != null ? business.getCategory().getName() : null)
                .ownerId(business.getOwner() != null ? business.getOwner().getId() : null)
                .description(business.getDescription())
                .evaluation(evalDto)
                .firstImageUrl(firstImage)
                .weekendDay(business.getWeekendDay())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{businessId}")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Mettre à jour un business (partiel)", description = "Tous les champs sont optionnels")
    public ResponseEntity<BusinessResponse> updateBusiness(
            Authentication authentication,
            @PathVariable Long businessId,
            @Valid @RequestBody BusinessUpdateRequest request
    ) {
        // Vérifier l’ownership
        var existing = businessService.getBusinessById(businessId, null)
                .orElseThrow(() -> new IllegalArgumentException("Business introuvable"));
        // authentication.getName() contains the user id (string)
        Long currentUserId;
        try {
            currentUserId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid authenticated user id");
        }

        if (existing.getOwner() == null || existing.getOwner().getId() == null ||
                !existing.getOwner().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("Vous ne pouvez modifier que votre propre business");
        }

        // Construire l’input partiel: ne définir que les champs présents
        Business input = new Business();
        if (request.getName() != null) input.setName(request.getName());
        if (request.getLocation() != null) input.setLocation(request.getLocation());
        if (request.getPhone() != null) input.setPhone(request.getPhone());
        if (request.getEmail() != null) input.setEmail(request.getEmail());
        if (request.getDescription() != null) input.setDescription(request.getDescription());
        if (request.getCategoryId() != null) {
            Category stub = new Category();
            stub.setId(request.getCategoryId());
            input.setCategory(stub);
        }
        if (request.getWeekendDay() != null) {
            String wd = request.getWeekendDay().trim();
            if (wd.isEmpty()) {
                // clear
                input.setWeekendDay(null);
            } else {
                try {
                    java.time.DayOfWeek dow = java.time.DayOfWeek.valueOf(wd.toUpperCase());
                    input.setWeekendDay(dow);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid weekendDay value: " + request.getWeekendDay());
                }
            }
        }

        var updated = businessService.updateBusiness(businessId, input, null);

        // If weekendDay was changed, trigger sync of staff availabilities
        if (request.getWeekendDay() != null) {
            try {
                int syncCount = weekendDaySyncService.syncBusinessWeekendDays(businessId);
                log.info("Weekend day updated for business id={}. Synced {} staff availabilities", businessId, syncCount);
            } catch (Exception ex) {
                log.error("Failed to sync weekend days after business update for id={}: {}", businessId, ex.getMessage(), ex);
                // Don't fail the whole update, just log the error
            }
        }

        BusinessEvaluationResponse evalDto = mapLatestEvaluation(updated);
        String firstImage = getFirstImageUrl(updated.getId());

        BusinessResponse response = BusinessResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .location(updated.getLocation())
                .phone(updated.getPhone())
                .email(updated.getEmail())
                .status(updated.getStatus())
                .categoryId(updated.getCategory() != null ? updated.getCategory().getId() : null)
                .categoryName(updated.getCategory() != null ? updated.getCategory().getName() : null)
                .ownerId(updated.getOwner() != null ? updated.getOwner().getId() : null)
                .description(updated.getDescription())
                .evaluation(evalDto)
                .firstImageUrl(firstImage)
                .weekendDay(updated.getWeekendDay())
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{businessId}/status")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER','ADMIN')")
    @Operation(summary = "Change business status", description = "Owner or Admin can change status (rules enforced)")
    public ResponseEntity<?> changeStatus(
            Authentication authentication,
            @PathVariable Long businessId,
            @RequestBody Map<String, String> body
    ) {
        Long actorId;
        try {
            actorId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid authenticated user id");
        }

        boolean actorIsAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) || "ADMIN".equals(a.getAuthority()));

        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            throw new IllegalArgumentException("status is required");
        }

        com.bookify.backendbookify_saas.models.enums.BusinessStatus newStatus;
        try {
            newStatus = com.bookify.backendbookify_saas.models.enums.BusinessStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value");
        }

        // Enforce PENDING special logic: if BO requests PENDING, check evaluation overall
        boolean requestedByOwner = !actorIsAdmin;

        var changed = businessService.changeBusinessStatus(businessId, newStatus, actorId, actorIsAdmin);

        // Decide response based on the actual saved status (service may auto-change PENDING -> ACTIVE)
        if (requestedByOwner) {
            var savedStatus = changed.getStatus();
            // If service auto-activated, inform owner
            if (savedStatus == com.bookify.backendbookify_saas.models.enums.BusinessStatus.ACTIVE) {
                var evalList = evaluationRepository.findByBusinessOrderByCreatedAtDesc(changed);
                int overall = 0;
                if (!evalList.isEmpty()) overall = evalList.get(0).getOverallScore();
                return ResponseEntity.ok(Map.of(
                        "message", "Congratulations! Your business has been activated.",
                        "overallScore", overall,
                        "status", "ACTIVE"
                ));
            }

            // If owner requested PENDING but it stayed PENDING, give the advice based on overall
            if (newStatus == com.bookify.backendbookify_saas.models.enums.BusinessStatus.PENDING) {
                var evalList = evaluationRepository.findByBusinessOrderByCreatedAtDesc(changed);
                int overall = 0;
                if (!evalList.isEmpty()) overall = evalList.get(0).getOverallScore();

                if (overall <= 70) {
                    return ResponseEntity.ok(Map.of(
                            "message", "Your business was submitted for review, but the evaluation overall score is <= 70.",
                            "overallScore", overall,
                            "advice", "Enhance your business information (name, email, description, location) to improve the evaluation or wait for an admin to activate it. Activation is not guaranteed.",
                            "status", "PENDING"
                    ));
                }

                // overall > 70 but service did not auto-activate for some reason
                return ResponseEntity.ok(Map.of(
                        "message", "Your business was submitted for review. The evaluation overall score is above 70 but activation still requires admin approval.",
                        "overallScore", overall,
                        "advice", "You can wait for an admin to activate it or further improve your information to maximize the chance of activation."
                ));
            }
        }

        return ResponseEntity.ok(Map.of("message", "Business status updated", "status", changed.getStatus()));
    }

    @DeleteMapping("/{businessId}")
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Delete (soft) business", description = "Owner can soft-delete their business; status will be set to DELETED")
    public ResponseEntity<?> deleteBusiness(
            Authentication authentication,
            @PathVariable Long businessId
    ) {
        Long ownerId = Long.parseLong(authentication.getName());
        // Use changeBusinessStatus to enforce ownership and set DELETED
        var changed = businessService.changeBusinessStatus(businessId, com.bookify.backendbookify_saas.models.enums.BusinessStatus.DELETED, ownerId, false);
        return ResponseEntity.noContent().build();
    }

    private BusinessEvaluationResponse mapLatestEvaluation(Business business) {
        List<BusinessEvaluation> list = evaluationRepository.findByBusinessOrderByCreatedAtDesc(business);
        if (list.isEmpty()) return null;
        BusinessEvaluation e = list.get(0);
        return BusinessEvaluationResponse.builder()
                .id(e.getId())
                .brandingScore(e.getBrandingScore())
                .nameProfessionalismScore(e.getNameProfessionalismScore())
                .emailProfessionalismScore(e.getEmailProfessionalismScore())
                .descriptionProfessionalismScore(e.getDescriptionProfessionalismScore())
                .locationScore(e.getLocationScore())
                .categoryScore(e.getCategoryScore())
                .overallScore(e.getOverallScore())
                .nameDetails(e.getNameDetails())
                .emailDetails(e.getEmailDetails())
                .descriptionDetails(e.getDescriptionDetails())
                .brandingDetails(e.getBrandingDetails())
                .locationDetails(e.getLocationDetails())
                .categoryDetails(e.getCategoryDetails())
                .nameSuggestions(e.getNameSuggestions())
                .emailSuggestions(e.getEmailSuggestions())
                .descriptionSuggestions(e.getDescriptionSuggestions())
                .brandingSuggestions(e.getBrandingSuggestions())
                .source(e.getSource())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private String getFirstImageUrl(Long businessId) {
        List<BusinessImage> images = imageRepository.findByBusinessIdOrderByDisplayOrderAsc(businessId);
        return images.isEmpty() ? null : images.get(0).getImageUrl();
    }
}
