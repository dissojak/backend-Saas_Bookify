package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Security service to check if a user can access staff-related endpoints
 * Handles authorization for both pure STAFF role and BUSINESS_OWNER acting as staff
 */
@Service
@RequiredArgsConstructor
public class StaffSecurityService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    /**
     * Check if a user can access as staff for a given business
     * Returns true if:
     * - User has STAFF role and belongs to this business
     * - User has BUSINESS_OWNER role and has a staff record in this business
     */
    public boolean canAccessAsStaff(Long userId, Long businessId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return false;
        }

        RoleEnum role = user.get().getRole();

        // Pure STAFF role: check if staff belongs to this business
        if (role == RoleEnum.STAFF) {
            Optional<Long> staffBusinessId = staffRepository.findBusinessIdById(userId);
            return staffBusinessId.isPresent() && staffBusinessId.get().equals(businessId);
        }

        // BUSINESS_OWNER: check if they have a staff record in this business
        if (role == RoleEnum.BUSINESS_OWNER) {
            Optional<Long> staffBusinessId = staffRepository.findBusinessIdById(userId);
            return staffBusinessId.isPresent() && staffBusinessId.get().equals(businessId);
        }

        return false;
    }

    /**
     * Check if a user is staff (either pure STAFF or BO with staff record)
     */
    public boolean isStaff(Long userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return false;
        }

        RoleEnum role = user.get().getRole();

        // Pure STAFF
        if (role == RoleEnum.STAFF) {
            return true;
        }

        // BO with staff record
        if (role == RoleEnum.BUSINESS_OWNER) {
            return staffRepository.findBusinessIdById(userId).isPresent();
        }

        return false;
    }

    /**
     * Check if a user is a BUSINESS_OWNER who also has a staff record
     */
    public boolean isBoActingAsStaff(Long userId) {
        var user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return false;
        }

        if (user.get().getRole() != RoleEnum.BUSINESS_OWNER) {
            return false;
        }

        return staffRepository.findBusinessIdById(userId).isPresent();
    }
}
