package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Service
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByBusinessAndActiveTrue(Business business);

    List<Service> findByTenantId(String tenantId);

    Optional<Service> findByIdAndTenantId(Long id, String tenantId);

    List<Service> findByBusinessIdAndActiveTrue(Long businessId);
}
