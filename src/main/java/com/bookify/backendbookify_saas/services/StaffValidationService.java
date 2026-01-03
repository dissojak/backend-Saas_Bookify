package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffValidationService {

    private final StaffRepository staffRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public boolean isStaffForBusiness(Long staffId, Long businessId) {
        log.info("staffValidation: start check staffId={}, businessId={}", staffId, businessId);

        // Fast-path: derived exists check (simplest and most reliable in JPA)
        try {
            boolean exists = staffRepository.existsByIdAndEmployerBusiness_Id(staffId, businessId);
            log.info("staffValidation: existsByIdAndEmployerBusiness_Id({}, {}) -> {}", staffId, businessId, exists);
            if (exists) return true;
        } catch (Exception e) {
            log.warn("staffValidation: existsByIdAndEmployerBusiness_Id failed for ({}, {}) -> {}", staffId, businessId, e.toString());
        }

        // Next: JPQL count query
        try {
            int count = staffRepository.countByIdAndBusinessId(staffId, businessId);
            log.info("staffValidation: countByIdAndBusinessId({}, {}) -> {}", staffId, businessId, count);
            if (count > 0) return true;
        } catch (Exception e) {
            log.warn("staffValidation: countByIdAndBusinessId failed for ({}, {}) -> {}", staffId, businessId, e.toString());
        }

        // Fallback: native query returning integer (handles numeric mapping surprises)
        try {
            var maybe = staffRepository.findBusinessIdIntById(staffId);
            log.info("staffValidation: findBusinessIdIntById({}) -> {}", staffId, maybe);
            if (maybe.isPresent()) {
                int found = maybe.get();
                boolean matches = ((long) found) == businessId.longValue();
                log.info("staffValidation: businessId int compare {} == {} -> {}", found, businessId, matches);
                return matches;
            }
        } catch (Exception e) {
            log.warn("staffValidation: findBusinessIdIntById failed for {} -> {}", staffId, e.toString());
        }

        log.info("staffValidation: final -> false for staffId={}, businessId={}", staffId, businessId);
        return false;
    }
}
