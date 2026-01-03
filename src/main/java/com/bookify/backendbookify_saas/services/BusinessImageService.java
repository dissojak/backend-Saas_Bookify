package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException;
import com.bookify.backendbookify_saas.models.dtos.BusinessImageResponse;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessImage;
import com.bookify.backendbookify_saas.repositories.BusinessImageRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessImageService {

    private final BusinessImageRepository imageRepository;
    private final BusinessRepository businessRepository;
    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:bookify}")
    private String folder;

    @Value("${cloudinary.public-id-prefix:bookify}")
    private String publicIdPrefix;

    /**
     * Upload image file to Cloudinary and save to database
     */
    @Transactional
    public BusinessImageResponse uploadImageFile(Long businessId, MultipartFile file, Long ownerId) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        if (business.getOwner() == null) {
            throw new IllegalArgumentException("Business has no owner");
        }

        if (!business.getOwner().getId().equals(ownerId)) {
            throw new UnauthorizedAccessException("You are not the owner of this business");
        }

        // Check image limit (max 6 images)
        List<BusinessImage> existing = imageRepository.findByBusinessIdOrderByDisplayOrderAsc(businessId);
        if (existing.size() >= 6) {
            throw new IllegalArgumentException("Maximum of 6 images allowed per business");
        }

        int nextOrder = existing.isEmpty() ? 0 : existing.get(existing.size() - 1).getDisplayOrder() + 1;
        String publicId = publicIdPrefix + "-business-" + businessId + "-" + System.currentTimeMillis();

        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", folder + "/businesses",
                    "public_id", publicId,
                    "overwrite", true
            ));

            String secureUrl = result.get("secure_url") != null ? result.get("secure_url").toString() : null;
            if (secureUrl == null) {
                throw new IllegalStateException("Failed to obtain uploaded image URL");
            }

            BusinessImage image = new BusinessImage();
            image.setBusiness(business);
            image.setImageUrl(secureUrl);
            image.setDisplayOrder(nextOrder);

            BusinessImage saved = imageRepository.save(image);
            log.info("Uploaded business image to Cloudinary: {} for business {}", secureUrl, businessId);

            return mapToResponse(saved);
        } catch (IOException e) {
            log.error("Cloudinary upload failed for business {}", businessId, e);
            throw new IllegalStateException("Unable to upload business image");
        }
    }

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

