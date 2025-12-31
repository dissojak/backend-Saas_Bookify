package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessRating;
import com.bookify.backendbookify_saas.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for BusinessRating operations.
 * Manages business-specific ratings from clients.
 */
@Repository
public interface BusinessRatingRepository extends JpaRepository<BusinessRating, Long> {

    /**
     * Find a rating by business and client (unique per business per client)
     */
    Optional<BusinessRating> findByBusinessAndClient(Business business, User client);

    /**
     * Find a rating by business ID and client ID
     */
    @Query("SELECT br FROM BusinessRating br WHERE br.business.id = :businessId AND br.client.id = :clientId")
    Optional<BusinessRating> findByBusinessIdAndClientId(@Param("businessId") Long businessId, @Param("clientId") Long clientId);

    /**
     * Find all ratings for a business
     */
    List<BusinessRating> findByBusiness(Business business);

    /**
     * Find all ratings for a business by ID
     */
    @Query("SELECT br FROM BusinessRating br WHERE br.business.id = :businessId")
    List<BusinessRating> findByBusinessId(@Param("businessId") Long businessId);

    /**
     * Find all ratings by a client
     */
    List<BusinessRating> findByClient(User client);

    /**
     * Find all ratings by client ID
     */
    @Query("SELECT br FROM BusinessRating br WHERE br.client.id = :clientId")
    List<BusinessRating> findByClientId(@Param("clientId") Long clientId);

    /**
     * Get average rating for a business
     */
    @Query("SELECT AVG(br.score) FROM BusinessRating br WHERE br.business.id = :businessId")
    Double findAverageRatingByBusinessId(@Param("businessId") Long businessId);

    /**
     * Count ratings for a business
     */
    @Query("SELECT COUNT(br) FROM BusinessRating br WHERE br.business.id = :businessId")
    Long countByBusinessId(@Param("businessId") Long businessId);

    /**
     * Check if a rating exists for a business by a client
     */
    @Query("SELECT COUNT(br) > 0 FROM BusinessRating br WHERE br.business.id = :businessId AND br.client.id = :clientId")
    boolean existsByBusinessIdAndClientId(@Param("businessId") Long businessId, @Param("clientId") Long clientId);
}
