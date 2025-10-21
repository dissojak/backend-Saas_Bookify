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
    public Business createBusinessForOwner(String ownerEmail, String name, String location, String phone, String email, Long categoryId) {
        // Récupérer l'utilisateur
        User user = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        if (!(user instanceof BusinessOwner owner)) {
            throw new IllegalArgumentException("Seul un propriétaire d'entreprise peut créer un business");
        }
        if (user.getStatus() != com.bookify.backendbookify_saas.models.enums.UserStatusEnum.VERIFIED) {
            throw new IllegalArgumentException("Votre compte doit être vérifié pour créer un business");
        }

        // Vérifier si cet owner a déjà un business
        if (businessRepository.existsByOwner(owner)) {
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
        business.setOwner((BusinessOwner) userRepository.findByEmail(ownerEmail).orElseThrow());
        business.setCategory(category);
        return businessRepository.save(business); // le listener déclenchera l'évaluation
    }

    @Override
    @Transactional
    public Business updateBusiness(Long id, Business businessInput, String tenantId) {
        Business existing = businessRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Business introuvable"));
        if (businessInput.getName() != null) existing.setName(businessInput.getName());
        if (businessInput.getLocation() != null) existing.setLocation(businessInput.getLocation());
        if (businessInput.getPhone() != null) existing.setPhone(businessInput.getPhone());
        if (businessInput.getEmail() != null) existing.setEmail(businessInput.getEmail());
        if (businessInput.getCategory() != null && businessInput.getCategory().getId() != null) {
            Category cat = entityManager.find(Category.class, businessInput.getCategory().getId());
            if (cat == null) {
                throw new IllegalArgumentException("Catégorie introuvable");
            }
            existing.setCategory(cat);
        }
        return businessRepository.save(existing); // le listener déclenchera l'évaluation
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
