package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.models.dtos.BusinessSearchDto;
import com.bookify.backendbookify_saas.models.enums.BusinessStatus;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.BusinessEvaluationRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.services.BusinessService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessEvaluationService evaluationService; // conservé pour usage futur mais non appelé ici
    private final BusinessEvaluationRepository evaluationRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Business createBusiness(Business business) {
        return businessRepository.save(business);
    }

    @Override
    @Transactional
    public Business createBusinessForOwner(Long ownerId, String name, String location, String phone, String email, Long categoryId, String description) {
        // Récupérer l'utilisateur par ID (le token contient l'ID en subject)
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (user.getRole() != com.bookify.backendbookify_saas.models.enums.RoleEnum.BUSINESS_OWNER) {
            throw new IllegalArgumentException("Only a business owner can create a business");
        }
        if (user.getStatus() != com.bookify.backendbookify_saas.models.enums.UserStatusEnum.VERIFIED) {
            throw new IllegalArgumentException("Your account must be verified to create a business");
        }

        // Vérifier si cet owner a déjà un business
        if (businessRepository.existsByOwner(user)) {
            throw new IllegalArgumentException("You already have a business associated with your account");
        }

        // Optionnel: vérifier unicité du nom
        businessRepository.findByName(name).ifPresent(b -> {
            throw new IllegalArgumentException("A business with that name already exists");
        });

        // Charger la catégorie via EntityManager
        Category category = entityManager.find(Category.class, categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        Business business = new Business();
        business.setName(name);
        business.setLocation(location);
        business.setPhone(phone);
        business.setEmail(email);
        business.setDescription(description);
        business.setOwner(user);
        business.setCategory(category);
        Business saved = businessRepository.save(business);

        // Trigger evaluation after business is fully persisted
        evaluationService.evaluateAndSave(saved);

        return saved;
    }

    @Override
    @Transactional
    public Business updateBusiness(Long id, Business businessInput, String tenantId) {
        Business existing = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Track which fields were updated
        boolean nameChanged = false;
        boolean emailChanged = false;
        boolean phoneChanged = false;
        boolean locationChanged = false;
        boolean descriptionChanged = false;
        boolean categoryChanged = false;
        boolean weekendChanged = false;

        if (businessInput.getName() != null) {
            nameChanged = !businessInput.getName().equals(existing.getName());
            existing.setName(businessInput.getName());
        }
        if (businessInput.getLocation() != null) {
            locationChanged = !businessInput.getLocation().equals(existing.getLocation());
            existing.setLocation(businessInput.getLocation());
        }
        if (businessInput.getPhone() != null) {
            phoneChanged = !businessInput.getPhone().equals(existing.getPhone());
            existing.setPhone(businessInput.getPhone());
        }
        if (businessInput.getEmail() != null) {
            emailChanged = !businessInput.getEmail().equals(existing.getEmail());
            existing.setEmail(businessInput.getEmail());
        }
        if (businessInput.getDescription() != null) {
            descriptionChanged = (existing.getDescription() == null ||
                                !businessInput.getDescription().equals(existing.getDescription()));
            existing.setDescription(businessInput.getDescription());
        }
        if (businessInput.getCategory() != null && businessInput.getCategory().getId() != null) {
            Category cat = entityManager.find(Category.class, businessInput.getCategory().getId());
            if (cat == null) {
                throw new IllegalArgumentException("Category not found");
            }
            categoryChanged = existing.getCategory() == null ||
                            !existing.getCategory().getId().equals(cat.getId());
            existing.setCategory(cat);
        }

        // Weekend day: single nullable DayOfWeek stored on Business
        if (businessInput.getWeekendDay() != null) {
            weekendChanged = existing.getWeekendDay() == null || !existing.getWeekendDay().equals(businessInput.getWeekendDay());
            existing.setWeekendDay(businessInput.getWeekendDay());
        }
        Business updated = businessRepository.save(existing);

        // Only update evaluation if relevant fields changed
        if (nameChanged || emailChanged || phoneChanged || locationChanged ||
            descriptionChanged || categoryChanged) {
            evaluationService.updateEvaluation(updated, nameChanged, emailChanged,
                phoneChanged, locationChanged, descriptionChanged, categoryChanged);
        }

        return updated;
    }

    @Override
    public Optional<Business> getBusinessById(Long id, String tenantId) {
        return businessRepository.findById(id);
    }

    @Override
    public Optional<Business> getBusinessByOwnerId(Long ownerId) {
        User owner = userRepository.findById(ownerId).orElse(null);
        if (owner == null) {
            return Optional.empty();
        }
        return businessRepository.findByOwner(owner);
    }

    @Override
    public Business changeBusinessStatus(Long businessId, com.bookify.backendbookify_saas.models.enums.BusinessStatus newStatus, Long actorId, boolean actorIsAdmin) {
        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found"));

        // Owner check if needed
        Long ownerId = business.getOwner() != null ? business.getOwner().getId() : null;

        switch (newStatus) {
            case PENDING -> {
                // Only business owner (not admin) can request PENDING
                if (actorIsAdmin) {
                    throw new com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException("Admins cannot set a business to PENDING");
                }
                if (ownerId == null || !ownerId.equals(actorId)) {
                    throw new com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException("You are not the owner of this business");
                }
                // Check latest evaluation: if overall > 70, auto-activate
                try {
                    List<BusinessEvaluation> evals = evaluationRepository.findByBusinessOrderByCreatedAtDesc(business);
                    if (!evals.isEmpty()) {
                        int overall = evals.get(0).getOverallScore();
                        if (overall > 70) {
                            log.info("Auto-activating business {} because overall score {} > 70", businessId, overall);
                            business.setStatus(com.bookify.backendbookify_saas.models.enums.BusinessStatus.ACTIVE);
                            break;
                        }
                    }
                } catch (Exception ex) {
                    log.warn("Failed to read evaluations for business {}: {}", businessId, ex.getMessage());
                    // Fall back to PENDING
                }
                business.setStatus(newStatus);
            }
            case ACTIVE -> {
                // Only admin can activate
                if (!actorIsAdmin) {
                    throw new com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException("Only admin can activate a business");
                }
                business.setStatus(newStatus);
            }
            case SUSPENDED -> {
                // Only admin can suspend
                if (!actorIsAdmin) {
                    throw new com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException("Only admin can suspend a business");
                }
                business.setStatus(newStatus);
            }
            case DELETED -> {
                // Only business owner can delete (soft-delete)
                if (ownerId == null || !ownerId.equals(actorId)) {
                    throw new com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException("Only the business owner can delete the business");
                }
                business.setStatus(newStatus);
            }
            case INACTIVE -> {
                // Only business owner can set inactive
                if (ownerId == null || !ownerId.equals(actorId)) {
                    throw new com.bookify.backendbookify_saas.exceptions.UnauthorizedAccessException("Only the business owner can set the business to INACTIVE");
                }
                business.setStatus(newStatus);
            }
            default -> throw new IllegalArgumentException("Unsupported status change");
        }

        return businessRepository.save(business);
    }

    @Override
    public List<Business> getAllBusinessesByOwner(User owner) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<Business> getAllBusinesses(String tenantId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void deleteBusiness(Long id, String tenantId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean existsByName(String name, String tenantId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String generateTenantId() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public java.util.List<BusinessSearchDto> searchByName(String query) {
        if (query == null || query.isBlank()) return java.util.List.of();
        // Search only active businesses
        java.util.List<Business> found = businessRepository.findByNameContainingIgnoreCaseAndStatus(query.trim(), BusinessStatus.ACTIVE);
        if (found == null || found.isEmpty()) return java.util.List.of();

        return found.stream().map(b -> {
            Double avg = null;
            try {
                if (b.getRatings() != null && !b.getRatings().isEmpty()) {
                    avg = b.getRatings().stream().mapToInt(r -> r.getScore()).average().orElse(Double.NaN);
                    if (Double.isNaN(avg)) avg = null;
                }
            } catch (Exception ignored) {}

            String img = null;
            try {
                if (b.getImages() != null && !b.getImages().isEmpty()) {
                    img = b.getImages().get(0).getImageUrl();
                }
            } catch (Exception ignored) {}

            Long catId = null; String catName = null;
            if (b.getCategory() != null) {
                catId = b.getCategory().getId();
                catName = b.getCategory().getName();
            }

            return BusinessSearchDto.builder()
                    .id(b.getId())
                    .name(b.getName())
                    .categoryId(catId)
                    .categoryName(catName)
                    .rating(avg)
                    .imageUrl(img)
                    .description(b.getDescription())
                    .build();
        }).collect(Collectors.toList());
    }
}
