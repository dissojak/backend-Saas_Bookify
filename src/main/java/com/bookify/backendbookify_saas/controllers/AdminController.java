package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.entities.Category;
import com.bookify.backendbookify_saas.models.enums.BusinessStatus;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.models.enums.UserStatusEnum;
import com.bookify.backendbookify_saas.repositories.*;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import com.bookify.backendbookify_saas.models.entities.User;

/**
 * REST Controller for Admin operations.
 * Provides dashboard statistics and admin management endpoints.
 */
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin dashboard and management endpoints")
@Slf4j
public class AdminController {

    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final ServiceBookingRepository bookingRepository;
    private final ServiceRatingRepository serviceRatingRepository;
    private final BusinessRatingRepository businessRatingRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Get admin dashboard statistics.
     * Returns counts for businesses, users, bookings, and pending actions.
     */
    @GetMapping("/stats")
    @Operation(summary = "Get admin dashboard stats", description = "Returns statistics for the admin dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Business stats
            long totalBusinesses = businessRepository.count();
            long pendingBusinesses = businessRepository.findByStatus(BusinessStatus.PENDING).size();
            long activeBusinesses = businessRepository.findByStatus(BusinessStatus.ACTIVE).size();
            long draftBusinesses = businessRepository.findByStatus(BusinessStatus.DRAFT).size();

            // User stats
            long totalUsers = userRepository.count();

            // Booking stats - today's bookings
            LocalDate today = LocalDate.now();
            long todayBookings = bookingRepository.findAll().stream()
                    .filter(booking -> booking.getDate() != null && booking.getDate().equals(today))
                    .count();

            // Low rating count (ratings <= 2 stars could be considered "flagged")
            long totalServiceRatings = serviceRatingRepository.count();
            long totalBusinessRatings = businessRatingRepository.count();

            long flaggedServiceRatings = serviceRatingRepository.findAll().stream()
                    .filter(rating -> rating.getScore() != null && rating.getScore() <= 2)
                    .count();
            long flaggedBusinessRatings = businessRatingRepository.findAll().stream()
                    .filter(rating -> rating.getScore() != null && rating.getScore() <= 2)
                    .count();
            long totalFlaggedRatings = flaggedServiceRatings + flaggedBusinessRatings;

            log.info("Rating stats - Service ratings: {}, Business ratings: {}, Flagged service: {}, Flagged business: {}",
                    totalServiceRatings, totalBusinessRatings, flaggedServiceRatings, flaggedBusinessRatings);

            stats.put("totalBusinesses", totalBusinesses);
            stats.put("pendingBusinesses", pendingBusinesses);
            stats.put("activeBusinesses", activeBusinesses);
            stats.put("draftBusinesses", draftBusinesses);
            stats.put("totalUsers", totalUsers);
            stats.put("todayBookings", todayBookings);
            stats.put("flaggedRatings", totalFlaggedRatings);
            stats.put("success", true);

            log.info("Admin stats retrieved: {} businesses ({} pending), {} users, {} bookings today",
                    totalBusinesses, pendingBusinesses, totalUsers, todayBookings);

        } catch (Exception e) {
            log.error("Error retrieving admin stats", e);
            stats.put("error", "Failed to retrieve statistics");
            stats.put("success", false);
            return ResponseEntity.internalServerError().body(stats);
        }

        return ResponseEntity.ok(stats);
    }

    /**
     * Get list of pending businesses for approval.
     */
    @GetMapping("/businesses/pending")
    @Operation(summary = "Get pending businesses", description = "Returns list of businesses awaiting approval")
    public ResponseEntity<?> getPendingBusinesses() {
        try {
            var pendingBusinesses = businessRepository.findByStatus(BusinessStatus.PENDING);
            return ResponseEntity.ok(pendingBusinesses);
        } catch (Exception e) {
            log.error("Error retrieving pending businesses", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve pending businesses"));
        }
    }

    /**
     * Get flagged ratings (low ratings that may need review).
     */
    @GetMapping("/ratings/flagged")
    @Operation(summary = "Get flagged ratings", description = "Returns ratings with 2 stars or less")
    public ResponseEntity<?> getFlaggedRatings() {
        try {
            var flaggedServiceRatings = serviceRatingRepository.findAll().stream()
                    .filter(rating -> rating.getScore() != null && rating.getScore() <= 2)
                    .map(rating -> {
                        Map<String, Object> ratingMap = new HashMap<>();
                        ratingMap.put("id", rating.getId());
                        ratingMap.put("score", rating.getScore());
                        ratingMap.put("comment", rating.getComment());
                        ratingMap.put("date", rating.getDate());
                        ratingMap.put("createdAt", rating.getCreatedAt());
                        ratingMap.put("service", rating.getService() != null ?
                            Map.of("id", rating.getService().getId(), "name", rating.getService().getName()) : null);
                        ratingMap.put("client", rating.getClient() != null ?
                            Map.of("id", rating.getClient().getId(), "name", rating.getClient().getName(), "email", rating.getClient().getEmail()) : null);
                        return ratingMap;
                    })
                    .toList();

            var flaggedBusinessRatings = businessRatingRepository.findAll().stream()
                    .filter(rating -> rating.getScore() != null && rating.getScore() <= 2)
                    .map(rating -> {
                        Map<String, Object> ratingMap = new HashMap<>();
                        ratingMap.put("id", rating.getId());
                        ratingMap.put("score", rating.getScore());
                        ratingMap.put("comment", rating.getComment());
                        ratingMap.put("date", rating.getDate());
                        ratingMap.put("createdAt", rating.getCreatedAt());
                        ratingMap.put("business", rating.getBusiness() != null ?
                            Map.of("id", rating.getBusiness().getId(), "name", rating.getBusiness().getName()) : null);
                        ratingMap.put("client", rating.getClient() != null ?
                            Map.of("id", rating.getClient().getId(), "name", rating.getClient().getName(), "email", rating.getClient().getEmail()) : null);
                        return ratingMap;
                    })
                    .toList();

            Map<String, Object> result = new HashMap<>();
            result.put("serviceRatings", flaggedServiceRatings);
            result.put("businessRatings", flaggedBusinessRatings);
            result.put("totalCount", flaggedServiceRatings.size() + flaggedBusinessRatings.size());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving flagged ratings", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve flagged ratings", "details", e.getMessage()));
        }
    }

    /**
     * Get all users for admin management.
     */
    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Returns list of all users in the system")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userRepository.findAll();

            // Convert to simple map to avoid serialization issues with complex entity relationships
            var simplifiedUsers = users.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("name", user.getName());
                userMap.put("email", user.getEmail());
                userMap.put("phoneNumber", user.getPhoneNumber());
                userMap.put("role", user.getRole() != null ? user.getRole().name() : null);
                userMap.put("status", user.getStatus() != null ? user.getStatus().name() : null);
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("updatedAt", user.getUpdatedAt());
                return userMap;
            }).toList();

            return ResponseEntity.ok(simplifiedUsers);
        } catch (Exception e) {
            log.error("Error retrieving users", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve users", "details", e.getMessage()));
        }
    }

    /**
     * Change the status of a business.
     */
    @PatchMapping("/businesses/{businessId}/status")
    @Operation(summary = "Change business status", description = "Updates the status of a business")
    public ResponseEntity<?> changeBusinessStatus(
            @PathVariable Long businessId,
            @RequestBody Map<String, String> request
    ) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }

            var business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new IllegalArgumentException("Business not found"));

            BusinessStatus status = BusinessStatus.valueOf(newStatus);
            business.setStatus(status);
            businessRepository.save(business);

            log.info("Business {} status changed to {}", businessId, newStatus);
            return ResponseEntity.ok(Map.of("message", "Business status updated successfully", "status", newStatus));
        } catch (IllegalArgumentException e) {
            log.error("Invalid status or business not found", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing business status", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to change business status"));
        }
    }

    /**
     * Ban a business (set status to SUSPENDED).
     */
    @PostMapping("/businesses/{businessId}/ban")
    @Operation(summary = "Ban a business", description = "Bans a business by setting its status to SUSPENDED")
    public ResponseEntity<?> banBusiness(@PathVariable Long businessId) {
        try {
            var business = businessRepository.findById(businessId)
                    .orElseThrow(() -> new IllegalArgumentException("Business not found"));

            business.setStatus(BusinessStatus.SUSPENDED);
            businessRepository.save(business);

            log.info("Business {} has been banned", businessId);
            return ResponseEntity.ok(Map.of("message", "Business banned successfully", "status", "SUSPENDED"));
        } catch (IllegalArgumentException e) {
            log.error("Business not found", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error banning business", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to ban business"));
        }
    }

    /**
     * Get all businesses (for admin panel - includes all statuses).
     */
    @GetMapping("/businesses/all")
    @Operation(summary = "Get all businesses", description = "Returns all businesses regardless of status (admin endpoint)")
    public ResponseEntity<?> getAllBusinesses() {
        try {
            var businesses = businessRepository.findAll();
            return ResponseEntity.ok(businesses);
        } catch (Exception e) {
            log.error("Error retrieving all businesses", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to retrieve businesses", "details", e.getMessage()));
        }
    }

    /**
     * Create a new category.
     */
    @PostMapping("/categories")
    @Operation(summary = "Create a category", description = "Creates a new category")
    public ResponseEntity<?> createCategory(Authentication authentication, @RequestBody Map<String, String> request) {
        try {
            User creator = resolveAuthenticatedUser(authentication);
            if (creator == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Authentication required"));
            }

            String name = request.get("name");
            String description = request.get("description");

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Category name is required"));
            }

            Category category = new Category();
            category.setName(name.trim());
            category.setDescription(description != null ? description.trim() : "");
            category.setCreatedBy(creator);

            Category saved = categoryRepository.save(category);
            log.info("Category created by {}: {}", creator.getId(), saved.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            log.error("Validation error creating category", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating category", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to create category", "details", e.getMessage()));
        }
    }

    /**
     * Update a category.
     */
    @PutMapping("/categories/{categoryId}")
    @Operation(summary = "Update a category", description = "Updates an existing category")
    public ResponseEntity<?> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody Map<String, String> request
    ) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Category not found"));

            String name = request.get("name");
            String description = request.get("description");

            if (name != null && !name.trim().isEmpty()) {
                category.setName(name.trim());
            }

            if (description != null) {
                category.setDescription(description.trim());
            }

            Category updated = categoryRepository.save(category);
            log.info("Category updated: {}", categoryId);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Category not found", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating category", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to update category", "details", e.getMessage()));
        }
    }

    /**
     * Delete a category.
     */
    @DeleteMapping("/categories/{categoryId}")
    @Operation(summary = "Delete a category", description = "Deletes a category")
    public ResponseEntity<?> deleteCategory(@PathVariable Long categoryId) {
        try {
            // Check if category exists before deleting
            if (!categoryRepository.existsById(categoryId)) {
                throw new IllegalArgumentException("Category not found");
            }

            categoryRepository.deleteById(categoryId);
            log.info("Category deleted: {}", categoryId);
            return ResponseEntity.ok(Map.of("message", "Category deleted successfully"));
        } catch (IllegalArgumentException e) {
            log.error("Category not found", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting category", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to delete category", "details", e.getMessage()));
        }
    }

    /**
     * Search users with optional filters (email, name, role, status, created date range).
     */
    @GetMapping("/users/search")
    @Operation(summary = "Search users", description = "Search users by email/name/role/status/created date range")
    public ResponseEntity<?> searchUsers(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) RoleEnum role,
            @RequestParam(required = false) UserStatusEnum status,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        try {
            LocalDateTime from = null;
            LocalDateTime to = null;
            if (fromDate != null && !fromDate.isBlank()) {
                from = LocalDateTime.parse(fromDate);
            }
            if (toDate != null && !toDate.isBlank()) {
                to = LocalDateTime.parse(toDate);
            }

            var users = userRepository.searchUsers(email, name, role, status, from, to);

            // Simplify response to avoid serialization issues
            var simplified = users.stream().map(user -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", user.getId());
                m.put("name", user.getName());
                m.put("email", user.getEmail());
                m.put("phoneNumber", user.getPhoneNumber());
                m.put("role", user.getRole() != null ? user.getRole().name() : null);
                m.put("status", user.getStatus() != null ? user.getStatus().name() : null);
                m.put("createdAt", user.getCreatedAt());
                return m;
            }).toList();

            return ResponseEntity.ok(simplified);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid date format. Use ISO-8601 (e.g., 2023-12-31T00:00:00)"));
        } catch (Exception e) {
            log.error("Error searching users", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to search users", "details", e.getMessage()));
        }
    }

    /**
     * Change user status.
     */
    @PatchMapping("/users/{userId}/status")
    @Operation(summary = "Change user status", description = "Updates the status of a user")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request
    ) {
        try {
            String newStatus = request.get("status");
            if (newStatus == null || newStatus.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            UserStatusEnum statusEnum = UserStatusEnum.valueOf(newStatus);

            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setStatus(statusEnum);
            userRepository.save(user);

            log.info("User {} status changed to {}", userId, newStatus);
            return ResponseEntity.ok(Map.of("message", "User status updated", "status", newStatus));
        } catch (IllegalArgumentException e) {
            log.error("Invalid status or user not found", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing user status", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to change user status"));
        }
    }

    /**
     * Ban a user (set status to SUSPENDED).
     */
    @PostMapping("/users/{userId}/ban")
    @Operation(summary = "Ban a user", description = "Bans a user by setting status to SUSPENDED")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        try {
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            user.setStatus(UserStatusEnum.SUSPENDED);
            userRepository.save(user);

            log.info("User {} has been banned", userId);
            return ResponseEntity.ok(Map.of("message", "User banned", "status", "SUSPENDED"));
        } catch (IllegalArgumentException e) {
            log.error("User not found", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error banning user", e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to ban user"));
        }
    }

    private User resolveAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return null;
        }
        String principal = authentication.getName();
        // Try numeric id first
        try {
            Long id = Long.parseLong(principal);
            return userRepository.findById(id).orElse(null);
        } catch (NumberFormatException ignored) {
            // Not a numeric id, fall back to email lookup
            return userRepository.findByEmail(principal).orElse(null);
        }
    }
}
