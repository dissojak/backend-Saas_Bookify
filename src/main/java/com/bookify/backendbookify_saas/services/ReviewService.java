package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.Booking;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Review;

import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des avis clients
 */
public interface ReviewService {

    Review createReview(Review review);

    Review updateReview(Long id, Review review, String tenantId);

    Optional<Review> getReviewById(Long id, String tenantId);

    List<Review> getReviewsByBooking(Booking booking);

    List<Review> getReviewsByBusiness(Business business, String tenantId);

    List<Review> getAllReviews(String tenantId);

    Double getAverageRatingForBusiness(Long businessId);

    void respondToReview(Long id, String response, String tenantId);

    void deleteReview(Long id, String tenantId);
}
