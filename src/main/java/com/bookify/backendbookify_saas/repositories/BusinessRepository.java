package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour l'entité Business
 * Relation 1:1 - Un propriétaire a un seul business
 */
@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    /**
     * Trouve le business associé à un propriétaire
     * @param owner Le propriétaire du business
     * @return Optional contenant le business ou vide si non trouvé
     */
    Optional<Business> findByOwner(BusinessOwner owner);

    /**
     * Trouve un business par son nom
     * @param name Le nom du business
     * @return Optional contenant le business ou vide si non trouvé
     */
    Optional<Business> findByName(String name);

    /**
     * Vérifie si un propriétaire a déjà un business
     * @param owner Le propriétaire
     * @return true si le propriétaire a déjà un business, false sinon
     */
    boolean existsByOwner(BusinessOwner owner);

    /**
     * Trouve le business associé à un propriétaire
     * @param ownerId Le propriétaire du business
     * @return Optional contenant le business ou vide si non trouvé
     */
    Optional<Business> findByOwnerId(Long ownerId);
}
