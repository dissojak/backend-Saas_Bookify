package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.RatingCreateRequest;
import com.bookify.backendbookify_saas.models.dtos.RatingResponse;
import com.bookify.backendbookify_saas.models.entities.*;
import com.bookify.backendbookify_saas.repositories.BusinessRatingRepository;
import com.bookify.backendbookify_saas.repositories.ServiceBookingRepository;
import com.bookify.backendbookify_saas.repositories.ServiceRatingRepository;
import com.bookify.backendbookify_saas.repositories.ServiceRepository;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing ratings using ServiceRating and BusinessRating entities.
 * Replaces the old Review-based approach.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl {

    private final ServiceRatingRepository serviceRatingRepository;
    private final BusinessRatingRepository businessRatingRepository;
    private final ServiceBookingRepository serviceBookingRepository;
    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    /**
     * Create or update ratings for a booking.
     * If ratings already exist for this service/business by the client, they will be updated.
     * At least one rating (service or business) must be provided.
     * 
     * Uses REQUIRES_NEW to get a fresh persistence context and avoid corruption from other queries.
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public RatingResponse createOrUpdateRating(RatingCreateRequest request, Long userId) {
        log.info("Creating/updating ratings for booking {} by user {}", request.getBookingId(), userId);

        // Validate at least one rating is provided
        if (request.getServiceRating() == null && request.getBusinessRating() == null) {
            throw new IllegalArgumentException("At least one rating (service or business) must be provided");
        }

        try {
            // Clear any potentially corrupted entities from previous operations
            entityManager.clear();
            
            // Use native query to get only the IDs we need - avoid loading entity proxies
            Object[] bookingInfo = (Object[]) entityManager.createNativeQuery(
                "SELECT b.id as booking_id, b.status, sb.client_id, sb.service_id, " +
                "s.name as service_name, s.business_id, bus.name as business_name, u.name as client_name " +
                "FROM bookings b " +
                "JOIN service_bookings sb ON sb.id = b.id " +
                "JOIN services s ON s.id = sb.service_id " +
                "LEFT JOIN businesses bus ON bus.id = s.business_id " +
                "LEFT JOIN users u ON u.id = sb.client_id " +
                "WHERE b.id = :bookingId")
                .setParameter("bookingId", request.getBookingId())
                .getSingleResult();
            
            if (bookingInfo == null) {
                throw new IllegalArgumentException("Booking not found");
            }
            
            Long bookingId = ((Number) bookingInfo[0]).longValue();
            String status = (String) bookingInfo[1];
            Long clientId = bookingInfo[2] != null ? ((Number) bookingInfo[2]).longValue() : null;
            Long serviceId = bookingInfo[3] != null ? ((Number) bookingInfo[3]).longValue() : null;
            String serviceName = (String) bookingInfo[4];
            Long businessId = bookingInfo[5] != null ? ((Number) bookingInfo[5]).longValue() : null;
            String businessName = (String) bookingInfo[6];
            String clientName = bookingInfo[7] != null ? (String) bookingInfo[7] : "Anonymous";
            
            // Verify the user owns this booking
            if (clientId == null || !clientId.equals(userId)) {
                throw new IllegalArgumentException("You can only rate your own bookings");
            }

            // Check if booking is completed
            if (status == null || !"COMPLETED".equals(status)) {
                throw new IllegalArgumentException("You can only rate completed bookings. Current status: " + status);
            }
            
            if (serviceId == null) {
                throw new IllegalArgumentException("Service not found for this booking");
            }

            ServiceRating serviceRating = null;
            BusinessRating businessRating = null;

            // Handle Service Rating using getReference() to avoid loading entire entity graph
            if (request.getServiceRating() != null) {
                Optional<ServiceRating> existingServiceRating = 
                        serviceRatingRepository.findByServiceIdAndClientId(serviceId, clientId);

                if (existingServiceRating.isPresent()) {
                    // Update existing service rating
                    serviceRating = existingServiceRating.get();
                    serviceRating.setScore(request.getServiceRating());
                    serviceRating.setComment(request.getServiceComment());
                    serviceRating.setDate(LocalDate.now());
                    log.info("Updated existing service rating ID: {}", serviceRating.getId());
                } else {
                    // Create new service rating using references
                    serviceRating = new ServiceRating();
                    serviceRating.setService(entityManager.getReference(com.bookify.backendbookify_saas.models.entities.Service.class, serviceId));
                    serviceRating.setClient(entityManager.getReference(User.class, clientId));
                    serviceRating.setScore(request.getServiceRating());
                    serviceRating.setComment(request.getServiceComment());
                    serviceRating.setDate(LocalDate.now());
                    log.info("Creating new service rating");
                }
                serviceRating = serviceRatingRepository.save(serviceRating);
            }

            // Handle Business Rating using getReference()
            if (request.getBusinessRating() != null && businessId != null) {
                Optional<BusinessRating> existingBusinessRating = 
                        businessRatingRepository.findByBusinessIdAndClientId(businessId, clientId);

                if (existingBusinessRating.isPresent()) {
                    // Update existing business rating
                    businessRating = existingBusinessRating.get();
                    businessRating.setScore(request.getBusinessRating());
                    businessRating.setComment(request.getBusinessComment());
                    businessRating.setDate(LocalDate.now());
                    log.info("Updated existing business rating ID: {}", businessRating.getId());
                } else {
                    // Create new business rating using references
                    businessRating = new BusinessRating();
                    businessRating.setBusiness(entityManager.getReference(Business.class, businessId));
                    businessRating.setClient(entityManager.getReference(User.class, clientId));
                    businessRating.setScore(request.getBusinessRating());
                    businessRating.setComment(request.getBusinessComment());
                    businessRating.setDate(LocalDate.now());
                    log.info("Creating new business rating");
                }
                businessRating = businessRatingRepository.save(businessRating);
            }

            // Build response using extracted primitive values
            return buildRatingResponseFromValues(bookingId, serviceId, serviceName, businessId, businessName, 
                    clientName, serviceRating, businessRating, true);
        } catch (IllegalArgumentException e) {
            log.error("Rating validation error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating/updating rating", e);
            throw new RuntimeException("Failed to process rating: " + e.getMessage(), e);
        }
    }

    /**
     * Get existing ratings for a booking.
     * Returns the ratings the client has given to the service and business from this booking.
     */
    @Transactional(readOnly = true)
    public RatingResponse getRatingForBooking(Long bookingId, Long userId) {
        log.info("Getting ratings for booking {} by user {}", bookingId, userId);

        // Find the booking with eager loading
        ServiceBooking booking = serviceBookingRepository.findByIdWithServiceAndBusiness(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Verify the user owns this booking
        if (booking.getClient() == null || !booking.getClient().getId().equals(userId)) {
            throw new IllegalArgumentException("You can only view ratings for your own bookings");
        }

        com.bookify.backendbookify_saas.models.entities.Service service = booking.getService();
        Business business = service != null ? service.getBusiness() : null;

        // IMPORTANT: Extract all primitive values NOW to avoid lazy loading issues
        Long extractedBookingId = booking.getId();
        Long serviceId = service != null ? service.getId() : null;
        String serviceName = service != null ? service.getName() : null;
        Long businessId = business != null ? business.getId() : null;
        String businessName = business != null ? business.getName() : null;
        String clientName = booking.getClient() != null ? booking.getClient().getName() : "Anonymous";

        // Find existing ratings
        Optional<ServiceRating> serviceRating = serviceId != null ?
                serviceRatingRepository.findByServiceIdAndClientId(serviceId, userId) :
                Optional.empty();
        
        Optional<BusinessRating> businessRating = businessId != null ? 
                businessRatingRepository.findByBusinessIdAndClientId(businessId, userId) : 
                Optional.empty();

        boolean hasExisting = serviceRating.isPresent() || businessRating.isPresent();

        return buildRatingResponseFromValues(extractedBookingId, serviceId, serviceName, businessId, businessName,
                clientName, serviceRating.orElse(null), businessRating.orElse(null), hasExisting);
    }

    /**
     * Check if a booking has been rated (either service or business rating exists).
     */
    @Transactional(readOnly = true)
    public boolean hasRating(Long bookingId, Long userId) {
        ServiceBooking booking = serviceBookingRepository.findByIdWithServiceAndBusiness(bookingId).orElse(null);
        if (booking == null || booking.getClient() == null || !booking.getClient().getId().equals(userId)) {
            return false;
        }

        com.bookify.backendbookify_saas.models.entities.Service service = booking.getService();
        if (service == null) {
            return false;
        }
        
        Business business = service.getBusiness();
        
        // Extract primitive IDs to avoid lazy loading issues
        Long serviceId = service.getId();
        Long businessId = business != null ? business.getId() : null;

        boolean hasServiceRating = serviceRatingRepository.existsByServiceIdAndClientId(serviceId, userId);
        boolean hasBusinessRating = businessId != null && 
                businessRatingRepository.existsByBusinessIdAndClientId(businessId, userId);

        return hasServiceRating || hasBusinessRating;
    }

    /**
     * Get all ratings by a user (across all their bookings).
     */
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByUser(Long userId) {
        List<ServiceBooking> userBookings = serviceBookingRepository.findByClientIdWithServiceAndBusiness(userId);
        
        return userBookings.stream()
                .filter(booking -> booking.getStatus() != null && 
                        booking.getStatus() == com.bookify.backendbookify_saas.models.enums.BookingStatusEnum.COMPLETED)
                .map(booking -> {
                    com.bookify.backendbookify_saas.models.entities.Service service = booking.getService();
                    Business business = service != null ? service.getBusiness() : null;
                    
                    if (service == null) {
                        return null;
                    }
                    
                    // IMPORTANT: Extract all primitive values NOW to avoid lazy loading issues
                    Long bookingId = booking.getId();
                    Long serviceId = service.getId();
                    String serviceName = service.getName();
                    Long businessId = business != null ? business.getId() : null;
                    String businessName = business != null ? business.getName() : null;
                    String clientName = booking.getClient() != null ? booking.getClient().getName() : "Anonymous";
                    
                    Optional<ServiceRating> serviceRating = 
                            serviceRatingRepository.findByServiceIdAndClientId(serviceId, userId);
                    Optional<BusinessRating> businessRating = businessId != null ?
                            businessRatingRepository.findByBusinessIdAndClientId(businessId, userId) :
                            Optional.empty();
                    
                    if (serviceRating.isEmpty() && businessRating.isEmpty()) {
                        return null;
                    }
                    
                    return buildRatingResponseFromValues(bookingId, serviceId, serviceName, businessId, businessName,
                            clientName, serviceRating.orElse(null), businessRating.orElse(null), true);
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    /**
     * Get average service rating for a service.
     */
    public Double getAverageServiceRating(Long serviceId) {
        return serviceRatingRepository.findAverageRatingByServiceId(serviceId);
    }

    /**
     * Get average business rating for a business.
     */
    public Double getAverageBusinessRating(Long businessId) {
        return businessRatingRepository.findAverageRatingByBusinessId(businessId);
    }

    /**
     * Get rating count for a service.
     */
    public Long getServiceRatingCount(Long serviceId) {
        return serviceRatingRepository.countByServiceId(serviceId);
    }

    /**
     * Get rating count for a business.
     */
    public Long getBusinessRatingCount(Long businessId) {
        return businessRatingRepository.countByBusinessId(businessId);
    }

    /**
     * Build a RatingResponse using primitive values instead of entities.
     * This completely avoids Hibernate lazy loading issues by never accessing entity proxies.
     */
    private RatingResponse buildRatingResponseFromValues(
            Long bookingId,
            Long serviceId,
            String serviceName,
            Long businessId,
            String businessName,
            String clientName,
            ServiceRating serviceRating, 
            BusinessRating businessRating,
            boolean hasExisting) {

        RatingResponse.RatingResponseBuilder builder = RatingResponse.builder()
                .bookingId(bookingId)
                .serviceId(serviceId)
                .serviceName(serviceName)
                .businessId(businessId)
                .businessName(businessName)
                .clientName(clientName != null ? clientName : "Anonymous")
                .hasExistingRating(hasExisting);

        if (serviceRating != null) {
            builder.serviceRatingId(serviceRating.getId())
                    .serviceRating(serviceRating.getScore())
                    .serviceComment(serviceRating.getComment())
                    .serviceRatingDate(serviceRating.getDate())
                    .createdAt(serviceRating.getCreatedAt());
        }

        if (businessRating != null) {
            builder.businessRatingId(businessRating.getId())
                    .businessRating(businessRating.getScore())
                    .businessComment(businessRating.getComment())
                    .businessRatingDate(businessRating.getDate());
            
            // Use business rating's createdAt if service rating doesn't have one
            if (serviceRating == null) {
                builder.createdAt(businessRating.getCreatedAt());
            }
        }

        return builder.build();
    }
}
