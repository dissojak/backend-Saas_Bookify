package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.ResourceTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceTemplateRepository extends JpaRepository<ResourceTemplate, Long> {

    /**
     * Find all templates for a business
     */
    List<ResourceTemplate> findByBusinessId(Long businessId);

    /**
     * Find a template by ID and ensure it belongs to the specified business
     */
    Optional<ResourceTemplate> findByIdAndBusinessId(Long id, Long businessId);

    /**
     * Find template with all attributes eager-loaded
     */
    @Query("SELECT t FROM ResourceTemplate t LEFT JOIN FETCH t.attributes WHERE t.id = :id")
    Optional<ResourceTemplate> findByIdWithAttributes(@Param("id") Long id);

    /**
     * Find template by ID and business with attributes eager-loaded
     */
    @Query("SELECT t FROM ResourceTemplate t LEFT JOIN FETCH t.attributes WHERE t.id = :id AND t.business.id = :businessId")
    Optional<ResourceTemplate> findByIdAndBusinessIdWithAttributes(@Param("id") Long id, @Param("businessId") Long businessId);
}
