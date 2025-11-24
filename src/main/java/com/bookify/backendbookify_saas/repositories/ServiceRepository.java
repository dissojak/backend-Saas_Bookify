package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.query.Param;

/**
 * Repository pour l'entité Service
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByBusinessAndActiveTrue(Business business);

    List<Service> findByTenantId(String tenantId);

    Optional<Service> findByIdAndTenantId(Long id, String tenantId);

    List<Service> findByBusinessIdAndActiveTrue(Long businessId);

    // Fetch a service together with its staff list and the creator to avoid LazyInitializationException
    @Query("SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.staff LEFT JOIN FETCH s.createdBy WHERE s.id = :id")
    Optional<Service> findByIdWithStaffAndCreator(@Param("id") Long id);
}
