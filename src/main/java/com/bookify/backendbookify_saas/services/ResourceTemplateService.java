package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing resource templates
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceTemplateService {

    private final ResourceTemplateRepository templateRepository;
    private final BusinessRepository businessRepository;

    /**
     * Create a new template
     */
    public ResourceTemplateResponse createTemplate(Long businessId, ResourceTemplateCreateRequest request) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        ResourceTemplate template = new ResourceTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setBusiness(business);

        if (request.getAttributes() != null) {
            for (TemplateAttributeDTO attr : request.getAttributes()) {
                TemplateAttribute templateAttr = TemplateAttribute.builder()
                        .attributeKey(attr.getAttributeKey())
                        .attributeType(attr.getAttributeType())
                        .required(attr.getRequired() != null ? attr.getRequired() : false)
                        .displayOrder(attr.getDisplayOrder() != null ? attr.getDisplayOrder() : 0)
                        .build();
                template.addAttribute(templateAttr);
            }
        }

        ResourceTemplate saved = templateRepository.save(template);
        return mapToResponse(saved);
    }

    /**
     * Update an existing template
     */
    public ResourceTemplateResponse updateTemplate(Long templateId, Long businessId, ResourceTemplateCreateRequest request) {
        ResourceTemplate template = templateRepository.findByIdAndBusinessId(templateId, businessId)
                .orElseThrow(() -> new RuntimeException("Template not found"));

        if (request.getName() != null) template.setName(request.getName());
        if (request.getDescription() != null) template.setDescription(request.getDescription());

        if (request.getAttributes() != null) {
            template.getAttributes().clear();
            for (TemplateAttributeDTO attr : request.getAttributes()) {
                TemplateAttribute templateAttr = TemplateAttribute.builder()
                        .attributeKey(attr.getAttributeKey())
                        .attributeType(attr.getAttributeType())
                        .required(attr.getRequired() != null ? attr.getRequired() : false)
                        .displayOrder(attr.getDisplayOrder() != null ? attr.getDisplayOrder() : 0)
                        .build();
                template.addAttribute(templateAttr);
            }
        }

        ResourceTemplate updated = templateRepository.save(template);
        return mapToResponse(updated);
    }

    /**
     * Delete a template
     */
    public void deleteTemplate(Long templateId, Long businessId) {
        ResourceTemplate template = templateRepository.findByIdAndBusinessId(templateId, businessId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        
        templateRepository.delete(template);
    }

    /**
     * Get all templates for a business
     */
    public List<ResourceTemplateResponse> getTemplatesByBusiness(Long businessId) {
        return templateRepository.findByBusinessId(businessId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get single template by ID
     */
    public ResourceTemplateResponse getTemplateById(Long templateId, Long businessId) {
        ResourceTemplate template = templateRepository.findByIdAndBusinessIdWithAttributes(templateId, businessId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        return mapToResponse(template);
    }

    private ResourceTemplateResponse mapToResponse(ResourceTemplate template) {
        return ResourceTemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .businessId(template.getBusiness().getId())
                .attributes(template.getAttributes().stream()
                        .map(attr -> new TemplateAttributeDTO(attr.getAttributeKey(), attr.getAttributeType(),
                                attr.getRequired(), attr.getDisplayOrder()))
                        .collect(Collectors.toList()))
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
}
