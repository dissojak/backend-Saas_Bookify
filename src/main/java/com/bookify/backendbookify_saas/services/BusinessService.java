package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des entreprises
 */
public interface BusinessService {

    Business createBusiness(Business business);

    // Ajout pour création par un propriétaire authentifié (ownerId est l'ID numérique extrait du token)
    Business createBusinessForOwner(Long ownerId, String name, String location, String phone, String email, Long categoryId, String description);

    Business updateBusiness(Long id, Business business, String tenantId);

    Optional<Business> getBusinessById(Long id, String tenantId);

    Optional<Business> getBusinessByOwnerId(Long ownerId);

    // Change business status with actor info (owner or admin)
    Business changeBusinessStatus(Long businessId, com.bookify.backendbookify_saas.models.enums.BusinessStatus newStatus, Long actorId, boolean actorIsAdmin);

    List<Business> getAllBusinessesByOwner(User owner);

    List<Business> getAllBusinesses(String tenantId);

    void deleteBusiness(Long id, String tenantId);

    boolean existsByName(String name, String tenantId);

    String generateTenantId();
}
