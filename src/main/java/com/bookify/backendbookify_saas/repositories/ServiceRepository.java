package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    // Find all services for a business (including inactive)
    List<Service> findByBusinessId(Long businessId);

    // Fetch services that a given staff member is linked to (active only)
    List<Service> findByStaff_IdAndActiveTrue(Long staffId);

    // Fetch a service together with its staff list and the creator to avoid LazyInitializationException
    @Query("SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.staff LEFT JOIN FETCH s.createdBy WHERE s.id = :id")
    Optional<Service> findByIdWithStaffAndCreator(@Param("id") Long id);

    // Partial update for simple scalar fields to avoid flushing collections and cascades
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE Service s SET s.name = COALESCE(:name, s.name), s.description = COALESCE(:description, s.description), s.durationMinutes = COALESCE(:durationMinutes, s.durationMinutes), s.price = COALESCE(:price, s.price), s.imageUrl = COALESCE(:imageUrl, s.imageUrl), s.active = COALESCE(:active, s.active) WHERE s.id = :id")
    int updatePartial(@Param("id") Long id,
                      @Param("name") String name,
                      @Param("description") String description,
                      @Param("durationMinutes") Integer durationMinutes,
                      @Param("price") java.math.BigDecimal price,
                      @Param("imageUrl") String imageUrl,
                      @Param("active") Boolean active);
}
