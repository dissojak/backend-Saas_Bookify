package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.enums.BusinessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    Optional<Business> findByOwner(User owner);

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
    boolean existsByOwner(User owner);

    /**
     * Trouve le business associé à un propriétaire
     * @param ownerId Le propriétaire du business
     * @return Optional contenant le business ou vide si non trouvé
     */
    Optional<Business> findByOwnerId(Long ownerId);

    /**
     * Find all businesses with the given status
     */
    List<Business> findByStatus(BusinessStatus status);

    /**
     * New: case-insensitive partial search by name, filtering by status
     */
    List<Business> findByNameContainingIgnoreCaseAndStatus(String name, BusinessStatus status);

    /**
     * Search by location (case-insensitive partial match) and status
     */
    List<Business> findByLocationContainingIgnoreCaseAndStatus(String location, BusinessStatus status);

    /**
     * Search by category and status
     */
    List<Business> findByCategoryIdAndStatus(Long categoryId, BusinessStatus status);

    /**
     * Search by name OR description (case-insensitive partial match) and status
     */
    @org.springframework.data.jpa.repository.Query("SELECT b FROM Business b WHERE b.status = :status AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Business> searchByQueryAndStatus(@org.springframework.data.repository.query.Param("query") String query, @org.springframework.data.repository.query.Param("status") BusinessStatus status);

    /**
     * Advanced search: query (name/description) + optional location + status
     */
    @org.springframework.data.jpa.repository.Query("SELECT b FROM Business b WHERE b.status = :status AND (LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND (:location IS NULL OR LOWER(b.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    List<Business> advancedSearch(@org.springframework.data.repository.query.Param("query") String query, @org.springframework.data.repository.query.Param("location") String location, @org.springframework.data.repository.query.Param("status") BusinessStatus status);

    /**
     * Advanced search with category filter
     */
    @org.springframework.data.jpa.repository.Query("SELECT b FROM Business b WHERE b.status = :status AND (:query IS NULL OR LOWER(b.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(b.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND (:location IS NULL OR LOWER(b.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND (:categoryId IS NULL OR b.category.id = :categoryId)")
    List<Business> fullSearch(@org.springframework.data.repository.query.Param("query") String query, @org.springframework.data.repository.query.Param("location") String location, @org.springframework.data.repository.query.Param("categoryId") Long categoryId, @org.springframework.data.repository.query.Param("status") BusinessStatus status);
}
