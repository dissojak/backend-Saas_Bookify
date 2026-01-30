package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.ResourceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceImageRepository extends JpaRepository<ResourceImage, Long> {

    /**
     * Find all images for a resource, ordered by display order
     */
    List<ResourceImage> findByResourceIdOrderByDisplayOrder(Long resourceId);

    /**
     * Find primary image for a resource
     */
    Optional<ResourceImage> findByResourceIdAndIsPrimary(Long resourceId, Boolean isPrimary);

    /**
     * Find image by ID ensuring it belongs to the resource
     */
    Optional<ResourceImage> findByIdAndResourceId(Long id, Long resourceId);
}
