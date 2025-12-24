package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Booking;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Review
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBooking(Booking booking);

    @Query("SELECT r FROM Review r JOIN ServiceBooking sb ON r.booking.id = sb.id JOIN sb.service s WHERE s.business = :business AND r.tenantId = :tenantId")
    List<Review> findByBooking_BusinessAndTenantId(@Param("business") Business business, @Param("tenantId") String tenantId);

    Optional<Review> findByIdAndTenantId(Long id, String tenantId);

    List<Review> findByTenantId(String tenantId);

    @Query("SELECT AVG(r.rating) FROM Review r JOIN ServiceBooking sb ON r.booking.id = sb.id JOIN sb.service s WHERE s.business.id = :businessId")
    Double findAverageRatingByBusinessId(@Param("businessId") Long businessId);

    @Query("SELECT COUNT(r) FROM Review r JOIN ServiceBooking sb ON r.booking.id = sb.id JOIN sb.service s WHERE s.business.id = :businessId")
    Long countReviewsByBusinessId(@Param("businessId") Long businessId);
}
