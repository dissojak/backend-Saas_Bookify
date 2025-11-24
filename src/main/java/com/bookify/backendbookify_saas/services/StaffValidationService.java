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
        try {
            int count = staffRepository.countByIdAndBusinessId(staffId, businessId);
            log.info("staffValidation: countByIdAndBusinessId({}, {}) -> {}", staffId, businessId, count);
            if (count > 0) return true;
        } catch (Exception e) {
            log.warn("staffValidation: countByIdAndBusinessId failed for ({}, {}) -> {}", staffId, businessId, e.toString());
        }

        try {
            var maybe = staffRepository.findBusinessIdIntById(staffId);
            log.info("staffValidation: findBusinessIdIntById({}) -> {}", staffId, maybe);
            if (maybe.isPresent()) {
                int found = maybe.get();
                return ((long) found) == businessId.longValue();
            }
        } catch (Exception e) {
            log.warn("staffValidation: findBusinessIdIntById failed for {} -> {}", staffId, e.toString());
        }

        // Last attempt: derived exists (should also work)
        try {
            boolean exists = staffRepository.existsByIdAndBusiness_Id(staffId, businessId);
            log.info("staffValidation: existsByIdAndBusiness_Id({}, {}) -> {}", staffId, businessId, exists);
            return exists;
        } catch (Exception e) {
            log.warn("staffValidation: existsByIdAndBusiness_Id failed for ({}, {}) -> {}", staffId, businessId, e.toString());
        }

        return false;
    }
}
