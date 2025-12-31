package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.ServiceRating;
import com.bookify.backendbookify_saas.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ServiceRating operations.
 * Manages service-specific ratings from clients.
 */
@Repository
public interface ServiceRatingRepository extends JpaRepository<ServiceRating, Long> {

    /**
     * Find a rating by service and client (unique per service per client)
     */
    Optional<ServiceRating> findByServiceAndClient(Service service, User client);

    /**
     * Find a rating by service ID and client ID
     */
    @Query("SELECT sr FROM ServiceRating sr WHERE sr.service.id = :serviceId AND sr.client.id = :clientId")
    Optional<ServiceRating> findByServiceIdAndClientId(@Param("serviceId") Long serviceId, @Param("clientId") Long clientId);

    /**
     * Find all ratings for a service
     */
    List<ServiceRating> findByService(Service service);

    /**
     * Find all ratings for a service by ID
     */
    @Query("SELECT sr FROM ServiceRating sr WHERE sr.service.id = :serviceId")
    List<ServiceRating> findByServiceId(@Param("serviceId") Long serviceId);

    /**
     * Find all ratings by a client
     */
    List<ServiceRating> findByClient(User client);

    /**
     * Find all ratings by client ID
     */
    @Query("SELECT sr FROM ServiceRating sr WHERE sr.client.id = :clientId")
    List<ServiceRating> findByClientId(@Param("clientId") Long clientId);

    /**
     * Get average rating for a service
     */
    @Query("SELECT AVG(sr.score) FROM ServiceRating sr WHERE sr.service.id = :serviceId")
    Double findAverageRatingByServiceId(@Param("serviceId") Long serviceId);

    /**
     * Count ratings for a service
     */
    @Query("SELECT COUNT(sr) FROM ServiceRating sr WHERE sr.service.id = :serviceId")
    Long countByServiceId(@Param("serviceId") Long serviceId);

    /**
     * Find all ratings for services belonging to a business
     */
    @Query("SELECT sr FROM ServiceRating sr WHERE sr.service.business.id = :businessId")
    List<ServiceRating> findByBusinessId(@Param("businessId") Long businessId);

    /**
     * Check if a rating exists for a service by a client
     */
    @Query("SELECT COUNT(sr) > 0 FROM ServiceRating sr WHERE sr.service.id = :serviceId AND sr.client.id = :clientId")
    boolean existsByServiceIdAndClientId(@Param("serviceId") Long serviceId, @Param("clientId") Long clientId);
}
