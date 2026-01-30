package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    /**
     * Find all resources for a business
     */
    List<Resource> findByBusinessId(Long businessId);

    /**
     * Find resource by ID ensuring it belongs to the business
     */
    Optional<Resource> findByIdAndBusinessId(Long id, Long businessId);

    /**
     * Find resource with all details eager-loaded
     */
    @Query("SELECT r FROM Resource r " +
           "LEFT JOIN FETCH r.attributes " +
           "LEFT JOIN FETCH r.pricingOptions " +
           "LEFT JOIN FETCH r.images " +
           "LEFT JOIN FETCH r.assignedStaff " +
           "WHERE r.id = :id")
    Optional<Resource> findByIdWithDetails(@Param("id") Long id);

    /**
     * Find all resources for a business with details eager-loaded
     */
    @Query("SELECT r FROM Resource r " +
           "LEFT JOIN FETCH r.attributes " +
           "LEFT JOIN FETCH r.pricingOptions " +
           "LEFT JOIN FETCH r.images " +
           "WHERE r.business.id = :businessId")
    List<Resource> findByBusinessIdWithDetails(@Param("businessId") Long businessId);

    /**
     * Search resources by query (name, description, type) - for public client search
     */
    @Query("SELECT r FROM Resource r " +
           "WHERE r.status = 'AVAILABLE' AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.type) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Resource> searchResources(@Param("query") String query, Pageable pageable);

    /**
     * Search resources with type filter
     */
    @Query("SELECT r FROM Resource r " +
           "WHERE r.status = 'AVAILABLE' AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "LOWER(r.type) = LOWER(:type)")
    Page<Resource> searchResourcesByType(@Param("query") String query, @Param("type") String type, Pageable pageable);

    /**
     * Find resources assigned to a staff member
     */
    @Query("SELECT r FROM Resource r " +
           "JOIN r.assignedStaff s " +
           "WHERE s.id = :staffId")
    List<Resource> findAssignedToStaff(@Param("staffId") Long staffId);

    /**
     * Find resources assigned to staff with details
     */
    @Query("SELECT r FROM Resource r " +
           "LEFT JOIN FETCH r.attributes " +
           "LEFT JOIN FETCH r.pricingOptions " +
           "LEFT JOIN FETCH r.images " +
           "JOIN r.assignedStaff s " +
           "WHERE s.id = :staffId")
    List<Resource> findAssignedToStaffWithDetails(@Param("staffId") Long staffId);
}
