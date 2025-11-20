package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.BusinessClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessClientRepository extends JpaRepository<BusinessClient, Long> {

    /**
     * Find all clients belonging to a specific business
     */
    List<BusinessClient> findByBusinessId(Long businessId);

    /**
     * Find a specific client by ID and business ID
     */
    Optional<BusinessClient> findByIdAndBusinessId(Long id, Long businessId);

    /**
     * Check if a phone number already exists for a business
     */
    boolean existsByBusinessIdAndPhone(Long businessId, String phone);

    /**
     * Delete all clients belonging to a business
     */
    void deleteByBusinessId(Long businessId);
}

