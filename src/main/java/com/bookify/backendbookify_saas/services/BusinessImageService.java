package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.BusinessImageResponse;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessImage;
import com.bookify.backendbookify_saas.repositories.BusinessImageRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessImageService {

    private final BusinessImageRepository imageRepository;
    private final BusinessRepository businessRepository;

    @Transactional
    public BusinessImageResponse uploadImage(Long businessId, String imageUrl, Long ownerId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Check if owner is null or doesn't match
        if (business.getOwner() == null) {
            throw new IllegalArgumentException("Business has no owner");
        }

        if (!business.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this business");
        }

        // Check if image URL already exists for this business
        if (imageRepository.existsByBusinessIdAndImageUrl(businessId, imageUrl)) {
            throw new IllegalArgumentException("This image URL already exists for this business");
        }

        List<BusinessImage> existing = imageRepository.findByBusinessIdOrderByDisplayOrderAsc(businessId);
        int nextOrder = existing.isEmpty() ? 0 : existing.get(existing.size() - 1).getDisplayOrder() + 1;

        BusinessImage image = new BusinessImage();
        image.setBusiness(business);
        image.setImageUrl(imageUrl);
        image.setDisplayOrder(nextOrder);

        BusinessImage saved = imageRepository.save(image);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<BusinessImageResponse> getBusinessImages(Long businessId) {
        return imageRepository.findByBusinessIdOrderByDisplayOrderAsc(businessId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteImage(Long imageId, Long ownerId) {
        BusinessImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found"));

        if (image.getBusiness() == null || image.getBusiness().getOwner() == null) {
            throw new IllegalArgumentException("Business or owner not found");
        }

        if (!image.getBusiness().getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this business");
        }

        imageRepository.delete(image);
    }

    @Transactional
    public void reorderImages(Long businessId, List<Long> imageIds, Long ownerId) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (business.getOwner() == null) {
            throw new IllegalArgumentException("Business has no owner");
        }

        if (!business.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this business");
        }

        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);
            BusinessImage image = imageRepository.findById(imageId)
                    .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

            if (!image.getBusiness().getId().equals(businessId)) {
                throw new IllegalArgumentException("Image does not belong to this business");
            }

            image.setDisplayOrder(i);
            imageRepository.save(image);
        }
    }

    private BusinessImageResponse mapToResponse(BusinessImage image) {
        return BusinessImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .displayOrder(image.getDisplayOrder())
                .uploadedAt(image.getUploadedAt())
                .build();
    }
}

