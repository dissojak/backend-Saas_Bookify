package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.services.BusinessService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final BusinessEvaluationService evaluationService; // conservé pour usage futur mais non appelé ici

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Business createBusiness(Business business) {
        return businessRepository.save(business);
    }

    @Override
    @Transactional
    public Business createBusinessForOwner(String ownerEmail, String name, String location, String phone, String email, Long categoryId, String description) {
        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        if (user.getRole() != com.bookify.backendbookify_saas.models.enums.RoleEnum.BUSINESS_OWNER) {
            throw new IllegalArgumentException("Seul un propriétaire d'entreprise peut créer un business");
        }
        if (user.getStatus() != com.bookify.backendbookify_saas.models.enums.UserStatusEnum.VERIFIED) {
            throw new IllegalArgumentException("Votre compte doit être vérifié pour créer un business");
        }

        // Vérifier si cet owner a déjà un business
        if (businessRepository.existsByOwner(user)) {
            throw new IllegalArgumentException("Vous avez déjà un business associé à votre compte");
        }

        // Optionnel: vérifier unicité du nom
        businessRepository.findByName(name).ifPresent(b -> {
            throw new IllegalArgumentException("Un business avec ce nom existe déjà");
        });

        // Charger la catégorie via EntityManager
        Category category = entityManager.find(Category.class, categoryId);
        if (category == null) {
            throw new IllegalArgumentException("Catégorie introuvable");
        }
        Business business = new Business();
        business.setName(name);
        business.setLocation(location);
        business.setPhone(phone);
        business.setEmail(email);
        business.setDescription(description);
        business.setOwner(userRepository.findByEmail(ownerEmail).orElseThrow());
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
                .orElseThrow(() -> new IllegalArgumentException("Business introuvable"));

        // Track which fields were updated
        boolean nameChanged = false;
        boolean emailChanged = false;
        boolean phoneChanged = false;
        boolean locationChanged = false;
        boolean descriptionChanged = false;
        boolean categoryChanged = false;

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
                throw new IllegalArgumentException("Catégorie introuvable");
            }
            categoryChanged = existing.getCategory() == null ||
                            !existing.getCategory().getId().equals(cat.getId());
            existing.setCategory(cat);
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
    public List<Business> getAllBusinessesByOwner(User owner) {
        throw new UnsupportedOperationException("Non implémenté");
    }

    @Override
    public List<Business> getAllBusinesses(String tenantId) {
        throw new UnsupportedOperationException("Non implémenté");
    }

    @Override
    public void deleteBusiness(Long id, String tenantId) {
        throw new UnsupportedOperationException("Non implémenté");
    }

    @Override
    public boolean existsByName(String name, String tenantId) {
        throw new UnsupportedOperationException("Non implémenté");
    }

    @Override
    public String generateTenantId() {
        throw new UnsupportedOperationException("Non implémenté");
    }
}
