package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.BusinessCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.BusinessResponse;
import com.bookify.backendbookify_saas.models.dtos.BusinessUpdateRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Category;
import com.bookify.backendbookify_saas.services.BusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/owner/businesses")
@RequiredArgsConstructor
@Tag(name = "Owner - Business", description = "Gestion des entreprises par le propriétaire")
public class BusinessOwnerController {

    private final BusinessService businessService;

    @PostMapping
    @PreAuthorize("hasRole('BUSINESS_OWNER')")
    @Operation(summary = "Créer un nouveau business", description = "Accessible uniquement aux BusinessOwner vérifiés")
    public ResponseEntity<BusinessResponse> createBusiness(
            Authentication authentication,
            @Valid @RequestBody BusinessCreateRequest request
    ) {
        String ownerEmail = authentication.getName();
        Business created = businessService.createBusinessForOwner(
                ownerEmail,
                request.getName(),
                request.getLocation(),
                request.getPhone(),
                request.getEmail(),
                request.getCategoryId()
        );

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
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
        String currentEmail = authentication.getName();
        if (existing.getOwner() == null || existing.getOwner().getEmail() == null ||
                !existing.getOwner().getEmail().equalsIgnoreCase(currentEmail)) {
            throw new IllegalArgumentException("Vous ne pouvez modifier que votre propre business");
        }

        // Construire l’input partiel: ne définir que les champs présents
        Business input = new Business();
        if (request.getName() != null) input.setName(request.getName());
        if (request.getLocation() != null) input.setLocation(request.getLocation());
        if (request.getPhone() != null) input.setPhone(request.getPhone());
        if (request.getEmail() != null) input.setEmail(request.getEmail());
        if (request.getCategoryId() != null) {
            Category stub = new Category();
            stub.setId(request.getCategoryId());
            input.setCategory(stub);
        }

        var updated = businessService.updateBusiness(businessId, input, null);

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
                .build();
        return ResponseEntity.ok(response);
    }
}
