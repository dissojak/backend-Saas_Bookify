package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.BusinessResponse;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Public endpoints to list businesses. Returns DTOs to avoid JPA proxy serialization issues.
 */
@RestController
@RequestMapping("/v1/businesses")
@RequiredArgsConstructor
public class BusinessPublicController {

    private final BusinessRepository businessRepository;

    @GetMapping
    public ResponseEntity<List<BusinessResponse>> listAll() {
        List<Business> list = businessRepository.findByStatus(com.bookify.backendbookify_saas.models.enums.BusinessStatus.ACTIVE);
        List<BusinessResponse> dto = list.stream().map(this::map).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getById(@PathVariable Long id) {
        return businessRepository.findById(id)
                .map(b -> ResponseEntity.ok(map(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private BusinessResponse map(Business b) {
        BusinessResponse.BusinessResponseBuilder builder = BusinessResponse.builder()
                .id(b.getId())
                .name(b.getName())
                .location(b.getLocation())
                .phone(b.getPhone())
                .email(b.getEmail())
                .status(b.getStatus())
                .description(b.getDescription());

        if (b.getCategory() != null) {
            builder.categoryId(b.getCategory().getId())
                    .categoryName(b.getCategory().getName());
        }
        if (b.getOwner() != null) {
            builder.ownerId(b.getOwner().getId());
        }
        // include weekendDay if present
        builder.weekendDay(b.getWeekendDay());

        return builder.build();
    }
}
