package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.exceptions.UserAlreadyStaffException;
import com.bookify.backendbookify_saas.services.StaffService;
import com.bookify.backendbookify_saas.services.StaffAvailabilityGeneratorService;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final StaffAvailabilityGeneratorService availabilityGeneratorService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createStaffRecord(Long userId, Long businessId) {
        try {
            int inserted = staffRepository.insertStaffRow(userId, businessId);
            if (inserted <= 0) {
                throw new IllegalStateException("Failed to insert staff row for userId=" + userId);
            }
        } catch (DataAccessException ex) {
            // If DB constraint (duplicate key), translate to domain exception
            String msg = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
            if (msg.contains("duplicate") || msg.contains("constraint") || msg.contains("unique")) {
                throw new UserAlreadyStaffException("User is already staff");
            }
            throw ex;
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createStaffAndSetRole(Long userId, Long businessId) {
        try {
            int updated = staffRepository.updateUserRole(userId, RoleEnum.STAFF.name());
            if (updated <= 0) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
            int inserted = staffRepository.insertStaffRow(userId, businessId);
            if (inserted <= 0) {
                throw new IllegalStateException("Failed to insert staff row for userId=" + userId);
            }
        } catch (Exception ex) {
            // Defensive: if any exception occurred (including ArrayIndexOutOfBounds or TransactionSystemException),
            // re-check DB: if staff row exists, convert to UserAlreadyStaffException so controller/handler returns 409.
            try {
                Optional<Long> maybe = staffRepository.findBusinessIdById(userId);
                if (maybe.isPresent()) {
                    throw new UserAlreadyStaffException("User is already staff");
                }
            } catch (Exception checkEx) {
                // ignore check failure and rethrow original
            }
            // If we didn't return above, rethrow the original exception
            if (ex instanceof RuntimeException) throw (RuntimeException) ex;
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long createStaffAndSetRoleWithWorkHours(Long userId, Long businessId, LocalTime startTime, LocalTime endTime) {
        try {
            // Update role
            int updated = staffRepository.updateUserRole(userId, RoleEnum.STAFF.name());
            if (updated <= 0) {
                throw new IllegalArgumentException("User not found: " + userId);
            }
            
            // Insert staff row with work hours
            // Convert LocalTime to String format for native SQL query
            String startTimeStr = startTime != null ? startTime.toString() : "09:00:00";
            String endTimeStr = endTime != null ? endTime.toString() : "17:00:00";
            int inserted = staffRepository.insertStaffRowWithWorkHours(userId, businessId, startTimeStr, endTimeStr);
            if (inserted <= 0) {
                throw new IllegalStateException("Failed to insert staff row for userId=" + userId);
            }
            
            // Auto-generate availabilities if both times are provided
            if (startTime != null && endTime != null) {
                try {
                    int generated = availabilityGeneratorService.generateForSingleStaff(userId, startTime, endTime);
                    log.info("Auto-generated {} availabilities for new staff {}", generated, userId);
                } catch (Exception genEx) {
                    log.error("Failed to auto-generate availabilities for new staff {}: {}", userId, genEx.getMessage());
                    // Don't fail the staff creation
                }
            }
            
            return userId;
        } catch (Exception ex) {
            // Defensive check
            try {
                Optional<Long> maybe = staffRepository.findBusinessIdById(userId);
                if (maybe.isPresent()) {
                    throw new UserAlreadyStaffException("User is already staff");
                }
            } catch (Exception checkEx) {
                // ignore
            }
            if (ex instanceof RuntimeException) throw (RuntimeException) ex;
            throw new RuntimeException(ex);
        }
    }
}
