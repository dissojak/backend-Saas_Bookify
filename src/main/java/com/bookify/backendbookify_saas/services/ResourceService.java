package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.*;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.models.enums.ResourceStatusEnum;
import com.bookify.backendbookify_saas.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing resources
 * Handles CRUD operations, staff assignments, and image management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceTemplateRepository templateRepository;
    private final ResourceImageRepository imageRepository;
    private final BusinessRepository businessRepository;
    private final StaffRepository staffRepository;
    private final StaffSecurityService staffSecurityService;

    /**
     * Create a new resource
     * Only business owners can create resources
     */
    public ResourceResponse createResource(Long businessId, Long actorId, ResourceCreateRequest request) {
        // Verify actor is business owner
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (!business.getOwner().getId().equals(actorId)) {
            throw new RuntimeException("Only business owner can create resources");
        }

        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setType(request.getType());
        resource.setStatus(ResourceStatusEnum.AVAILABLE);
        resource.setBusiness(business);

        // If template provided, validate and set it
        if (request.getTemplateId() != null) {
            ResourceTemplate template = templateRepository.findByIdAndBusinessId(request.getTemplateId(), businessId)
                    .orElseThrow(() -> new RuntimeException("Template not found"));
            resource.setTemplate(template);

            // Validate all required attributes from template are provided
            validateTemplateAttributes(template, request.getAttributes());
        }

        // Add attributes
        if (request.getAttributes() != null) {
            for (ResourceAttributeDTO attr : request.getAttributes()) {
                ResourceAttribute attribute = ResourceAttribute.builder()
                        .attributeKey(attr.getAttributeKey())
                        .attributeValue(attr.getAttributeValue())
                        .attributeType(attr.getAttributeType())
                        .displayOrder(attr.getDisplayOrder() != null ? attr.getDisplayOrder() : 0)
                        .build();
                resource.addAttribute(attribute);
            }
        }

        // Add pricing options
        if (request.getPricingOptions() != null) {
            for (ResourcePricingOptionDTO pricing : request.getPricingOptions()) {
                ResourcePricingOption option = ResourcePricingOption.builder()
                        .pricingType(pricing.getPricingType())
                        .price(pricing.getPrice())
                        .label(pricing.getLabel())
                        .minDuration(pricing.getMinDuration())
                        .maxDuration(pricing.getMaxDuration())
                        .isDefault(pricing.getIsDefault() != null ? pricing.getIsDefault() : false)
                        .build();
                resource.addPricingOption(option);
            }
        }

        // Add images
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ResourceImage image = ResourceImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .displayOrder(i)
                        .isPrimary(i == 0) // First image is primary
                        .build();
                resource.addImage(image);
            }
        }

        // Assign staff
        if (request.getStaffIds() != null) {
            for (Long staffId : request.getStaffIds()) {
                Staff staff = staffRepository.findById(staffId)
                        .orElseThrow(() -> new RuntimeException("Staff not found"));
                
                // Verify staff belongs to this business
                if (!staff.getEmployerBusiness().getId().equals(businessId)) {
                    throw new RuntimeException("Staff does not belong to this business");
                }
                
                resource.assignStaff(staff);
                // Also add to database via join table
                staffRepository.insertResourceStaffRow(resource.getId(), staffId);
            }
        }

        Resource saved = resourceRepository.save(resource);
        return mapToResponse(saved);
    }

    /**
     * Update an existing resource
     * Only owner or assigned staff can update (but staff cannot change assignments)
     */
    public ResourceResponse updateResource(Long resourceId, Long businessId, Long actorId, ResourceUpdateRequest request) {
        Resource resource = resourceRepository.findByIdAndBusinessId(resourceId, businessId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // Check permission: owner or assigned staff
        boolean isOwner = businessRepository.findById(businessId)
                .map(b -> b.getOwner().getId().equals(actorId))
                .orElse(false);
        
        boolean isAssignedStaff = staffSecurityService.canManageResource(actorId, resourceId);

        if (!isOwner && !isAssignedStaff) {
            throw new RuntimeException("Permission denied");
        }

        // Update basic fields
        if (request.getName() != null) resource.setName(request.getName());
        if (request.getDescription() != null) resource.setDescription(request.getDescription());
        if (request.getType() != null) resource.setType(request.getType());
        if (request.getStatus() != null) resource.setStatus(ResourceStatusEnum.valueOf(request.getStatus()));

        // Update attributes
        if (request.getAttributes() != null) {
            resource.getAttributes().clear();
            for (ResourceAttributeDTO attr : request.getAttributes()) {
                ResourceAttribute attribute = ResourceAttribute.builder()
                        .attributeKey(attr.getAttributeKey())
                        .attributeValue(attr.getAttributeValue())
                        .attributeType(attr.getAttributeType())
                        .displayOrder(attr.getDisplayOrder() != null ? attr.getDisplayOrder() : 0)
                        .build();
                resource.addAttribute(attribute);
            }
        }

        // Update pricing options
        if (request.getPricingOptions() != null) {
            resource.getPricingOptions().clear();
            for (ResourcePricingOptionDTO pricing : request.getPricingOptions()) {
                ResourcePricingOption option = ResourcePricingOption.builder()
                        .pricingType(pricing.getPricingType())
                        .price(pricing.getPrice())
                        .label(pricing.getLabel())
                        .minDuration(pricing.getMinDuration())
                        .maxDuration(pricing.getMaxDuration())
                        .isDefault(pricing.getIsDefault() != null ? pricing.getIsDefault() : false)
                        .build();
                resource.addPricingOption(option);
            }
        }

        // Update images (only owner can change staff - staff update blocked)
        if (request.getImageUrls() != null) {
            resource.getImages().clear();
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ResourceImage image = ResourceImage.builder()
                        .imageUrl(request.getImageUrls().get(i))
                        .displayOrder(i)
                        .isPrimary(i == 0)
                        .build();
                resource.addImage(image);
            }
        }

        // Update staff assignments (only owner)
        if (request.getStaffIds() != null && isOwner) {
            // Clear existing staff assignments
            List<Staff> currentStaff = List.copyOf(resource.getAssignedStaff());
            for (Staff staff : currentStaff) {
                staffRepository.deleteResourceStaffRow(resourceId, staff.getId());
                resource.unassignStaff(staff);
            }

            // Add new staff
            for (Long staffId : request.getStaffIds()) {
                Staff staff = staffRepository.findById(staffId)
                        .orElseThrow(() -> new RuntimeException("Staff not found"));
                
                if (!staff.getEmployerBusiness().getId().equals(businessId)) {
                    throw new RuntimeException("Staff does not belong to this business");
                }
                
                resource.assignStaff(staff);
                staffRepository.insertResourceStaffRow(resourceId, staffId);
            }
        }

        Resource updated = resourceRepository.save(resource);
        return mapToResponse(updated);
    }

    /**
     * Delete a resource (owner only)
     */
    public void deleteResource(Long resourceId, Long businessId, Long boUserId) {
        Resource resource = resourceRepository.findByIdAndBusinessId(resourceId, businessId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (!business.getOwner().getId().equals(boUserId)) {
            throw new RuntimeException("Only business owner can delete resources");
        }

        resourceRepository.delete(resource);
    }

    /**
     * Get all resources for a business
     */
    public List<ResourceResponse> getResourcesByBusiness(Long businessId) {
        return resourceRepository.findByBusinessIdWithDetails(businessId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get resource by ID
     */
    public ResourceResponse getResourceById(Long resourceId) {
        Resource resource = resourceRepository.findByIdWithDetails(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));
        return mapToResponse(resource);
    }

    /**
     * Get resources assigned to a staff member
     */
    public List<ResourceResponse> getResourcesByStaff(Long staffId) {
        return resourceRepository.findAssignedToStaffWithDetails(staffId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Assign staff to a resource (owner only)
     */
    public void assignStaffToResource(Long resourceId, Long staffId, Long boUserId, Long businessId) {
        Resource resource = resourceRepository.findByIdAndBusinessId(resourceId, businessId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (!business.getOwner().getId().equals(boUserId)) {
            throw new RuntimeException("Only business owner can assign staff");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (!staff.getEmployerBusiness().getId().equals(businessId)) {
            throw new RuntimeException("Staff does not belong to this business");
        }

        // Check if already assigned
        if (staffRepository.countResourceStaffRow(resourceId, staffId) > 0) {
            throw new RuntimeException("Staff already assigned to this resource");
        }

        resource.assignStaff(staff);
        staffRepository.insertResourceStaffRow(resourceId, staffId);
        resourceRepository.save(resource);
    }

    /**
     * Remove staff from a resource (owner only)
     */
    public void removeStaffFromResource(Long resourceId, Long staffId, Long boUserId, Long businessId) {
        Resource resource = resourceRepository.findByIdAndBusinessId(resourceId, businessId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        if (!business.getOwner().getId().equals(boUserId)) {
            throw new RuntimeException("Only business owner can remove staff");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        resource.unassignStaff(staff);
        staffRepository.deleteResourceStaffRow(resourceId, staffId);
        resourceRepository.save(resource);
    }

    /**
     * Add image to resource
     */
    public ResourceImageDTO addImage(Long resourceId, String imageUrl, boolean isPrimary) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        // If this is primary, unmark other images
        if (isPrimary) {
            resource.getImages().forEach(img -> img.setIsPrimary(false));
        }

        ResourceImage image = ResourceImage.builder()
                .imageUrl(imageUrl)
                .displayOrder(resource.getImages().size())
                .isPrimary(isPrimary)
                .build();

        resource.addImage(image);
        resourceRepository.save(resource);

        return mapImageToDTO(image);
    }

    /**
     * Remove image from resource
     */
    public void removeImage(Long resourceId, Long imageId) {
        ResourceImage image = imageRepository.findByIdAndResourceId(imageId, resourceId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        resource.removeImage(image);
        resourceRepository.save(resource);
    }

    /**
     * Reorder images
     */
    public void reorderImages(Long resourceId, List<Long> imageIds) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new RuntimeException("Resource not found"));

        for (int i = 0; i < imageIds.size(); i++) {
            final int index = i;
            ResourceImage image = resource.getImages().stream()
                    .filter(img -> img.getId().equals(imageIds.get(index)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Image not found"));
            image.setDisplayOrder(i);
        }

        resourceRepository.save(resource);
    }

    /**
     * Public search for resources (for clients)
     */
    public Page<ResourceResponse> searchResources(String query, Pageable pageable) {
        return resourceRepository.searchResources(query != null ? query : "", pageable)
                .map(this::mapToResponse);
    }

    /**
     * Search by type
     */
    public Page<ResourceResponse> searchResourcesByType(String query, String type, Pageable pageable) {
        return resourceRepository.searchResourcesByType(query != null ? query : "", type, pageable)
                .map(this::mapToResponse);
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private void validateTemplateAttributes(ResourceTemplate template, List<ResourceAttributeDTO> provided) {
        if (provided == null) provided = List.of();

        for (TemplateAttribute templateAttr : template.getAttributes()) {
            if (templateAttr.getRequired()) {
                boolean found = provided.stream()
                        .anyMatch(attr -> attr.getAttributeKey().equals(templateAttr.getAttributeKey())
                                && attr.getAttributeValue() != null);
                
                if (!found) {
                    throw new RuntimeException("Required attribute missing: " + templateAttr.getAttributeKey());
                }
            }
        }
    }

    private ResourceResponse mapToResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .name(resource.getName())
                .description(resource.getDescription())
                .type(resource.getType())
                .status(resource.getStatus().name())
                .templateId(resource.getTemplate() != null ? resource.getTemplate().getId() : null)
                .templateName(resource.getTemplate() != null ? resource.getTemplate().getName() : null)
                .attributes(resource.getAttributes().stream()
                        .map(attr -> new ResourceAttributeDTO(attr.getAttributeKey(), attr.getAttributeValue(), 
                                attr.getAttributeType(), attr.getDisplayOrder()))
                        .collect(Collectors.toList()))
                .pricingOptions(resource.getPricingOptions().stream()
                        .map(opt -> new ResourcePricingOptionDTO(opt.getId(), opt.getPricingType(), opt.getPrice(),
                                opt.getLabel(), opt.getMinDuration(), opt.getMaxDuration(), opt.getIsDefault()))
                        .collect(Collectors.toList()))
                .images(resource.getImages().stream()
                        .map(this::mapImageToDTO)
                        .collect(Collectors.toList()))
                .assignedStaff(resource.getAssignedStaff().stream()
                        .map(staff -> new StaffInfo(staff.getId(), staff.getName(), staff.getEmail(), staff.getAvatarUrl()))
                        .collect(Collectors.toList()))
                .businessId(resource.getBusiness().getId())
                .businessName(resource.getBusiness().getName())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }

    private ResourceImageDTO mapImageToDTO(ResourceImage image) {
        return ResourceImageDTO.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .isPrimary(image.getIsPrimary())
                .build();
    }
}
