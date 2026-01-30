package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.services.ResourceTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing resource templates
 * Only business owners can manage templates
 * Path: /v1/businesses/{businessId}/resource-templates
 */
@RestController
@RequestMapping("/v1/businesses/{businessId}/resource-templates")
@RequiredArgsConstructor
public class BusinessResourceTemplateController {

    private final ResourceTemplateService templateService;

    /**
     * Create a new resource template
     * POST /v1/businesses/{businessId}/resource-templates
     */
    @PostMapping
    public ResponseEntity<ResourceTemplateResponse> createTemplate(
            @PathVariable Long businessId,
            @RequestBody ResourceTemplateCreateRequest request) {
        ResourceTemplateResponse response = templateService.createTemplate(businessId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all templates for a business
     * GET /v1/businesses/{businessId}/resource-templates
     */
    @GetMapping
    public ResponseEntity<List<ResourceTemplateResponse>> getTemplates(
            @PathVariable Long businessId) {
        List<ResourceTemplateResponse> templates = templateService.getTemplatesByBusiness(businessId);
        return ResponseEntity.ok(templates);
    }

    /**
     * Get a specific template
     * GET /v1/businesses/{businessId}/resource-templates/{templateId}
     */
    @GetMapping("/{templateId}")
    public ResponseEntity<ResourceTemplateResponse> getTemplate(
            @PathVariable Long businessId,
            @PathVariable Long templateId) {
        ResourceTemplateResponse template = templateService.getTemplateById(templateId, businessId);
        return ResponseEntity.ok(template);
    }

    /**
     * Update a template
     * PUT /v1/businesses/{businessId}/resource-templates/{templateId}
     */
    @PutMapping("/{templateId}")
    public ResponseEntity<ResourceTemplateResponse> updateTemplate(
            @PathVariable Long businessId,
            @PathVariable Long templateId,
            @RequestBody ResourceTemplateCreateRequest request) {
        ResourceTemplateResponse response = templateService.updateTemplate(templateId, businessId, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a template
     * DELETE /v1/businesses/{businessId}/resource-templates/{templateId}
     */
    @DeleteMapping("/{templateId}")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long businessId,
            @PathVariable Long templateId) {
        templateService.deleteTemplate(templateId, businessId);
        return ResponseEntity.noContent().build();
    }
}
