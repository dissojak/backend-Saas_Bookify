package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Staff;
import com.bookify.backendbookify_saas.models.entities.StaffAvailability;
import com.bookify.backendbookify_saas.models.enums.AvailabilityStatus;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.StaffAvailabilityRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that generates staff availabilities for a date range.
 * Shared by CRON job and manual POST endpoint.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StaffAvailabilityGeneratorService {

    private final BusinessRepository businessRepository;
    private final StaffRepository staffRepository;
    private final StaffAvailabilityRepository availabilityRepository;

    /**
     * Generate availabilities for all businesses, 1 month ahead from today.
     * Used by daily CRON job.
     */
    @Transactional
    public int generateForAllBusinesses() {
        log.info("Starting daily staff availability generation for all businesses");
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(1);

        List<Business> businesses = businessRepository.findAll();
        log.info("Found {} businesses to process", businesses.size());

        int totalGenerated = 0;

        for (Business business : businesses) {
            try {
                int count = generateForBusiness(business.getId(), today, endDate);
                totalGenerated += count;
                log.info("Generated {} availabilities for business id={}", count, business.getId());
            } catch (Exception ex) {
                log.error("Failed to generate availabilities for business id={}: {}", business.getId(), ex.getMessage(), ex);
            }
        }

        log.info("Daily generation completed. Total availabilities created/updated: {}", totalGenerated);
        return totalGenerated;
    }

    /**
     * Generate availabilities for a specific business in a date range.
     *
     * @param businessId The business ID
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return Number of availabilities created or updated
     */
    @Transactional
    public int generateForBusiness(Long businessId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating staff availabilities for business id={} from {} to {}", businessId, startDate, endDate);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        // Get lightweight staff data (avoid loading full entities with collections to prevent ArrayIndexOutOfBounds)
        // Object[] contains: [id, defaultStartTime, defaultEndTime, name]
        List<Object[]> staffRows = staffRepository.findStaffBasicInfoByBusinessId(businessId);
        if (staffRows == null || staffRows.isEmpty()) {
            log.warn("No staff found for business id={} name={}", businessId, business.getName());
            return 0;
        }

        log.info("Found {} staff members for business id={} name={}", staffRows.size(), businessId, business.getName());

        int generatedCount = 0;

        for (Object[] row : staffRows) {
            Long staffId = ((Number) row[0]).longValue();
            java.sql.Time startTimeSql = (java.sql.Time) row[1];
            java.sql.Time endTimeSql = (java.sql.Time) row[2];
            String staffName = (String) row[3];

            java.time.LocalTime defaultStartTime = startTimeSql == null ? null : startTimeSql.toLocalTime();
            java.time.LocalTime defaultEndTime = endTimeSql == null ? null : endTimeSql.toLocalTime();

            // Rule 2: Skip staff without default times and log warning
            if (defaultStartTime == null || defaultEndTime == null) {
                log.warn("Skipping staff id={} name={} (no default working times set)", staffId, staffName);
                continue;
            }

            try {
                int count = generateForStaffLightweight(business, staffId, staffName, defaultStartTime, defaultEndTime, startDate, endDate);
                generatedCount += count;
            } catch (Exception ex) {
                log.error("Failed to generate availabilities for staff id={}: {}", staffId, ex.getMessage(), ex);
            }
        }

        return generatedCount;
    }

    /**
     * Generate availabilities for a single staff member in a date range (lightweight version).
     * Uses only the essential staff data to avoid loading full entities.
     */
    private int generateForStaffLightweight(Business business, Long staffId, String staffName,
                                           java.time.LocalTime defaultStartTime, java.time.LocalTime defaultEndTime,
                                           LocalDate startDate, LocalDate endDate) {
        int count = 0;
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            // Rule 3: Check if it's a closed day (Sunday OR business weekend_day)
            boolean isClosedDay = current.getDayOfWeek() == DayOfWeek.SUNDAY ||
                                 (business.getWeekendDay() != null && current.getDayOfWeek() == business.getWeekendDay());

            // Rule 4: Check existing availability
            var existingOpt = availabilityRepository.findByStaff_IdAndDate(staffId, current);

            if (existingOpt.isPresent()) {
                StaffAvailability existing = existingOpt.get();

                // Rule 4a: Do NOT overwrite if user-edited (updatedAt > createdAt)
                if (existing.getUpdatedAt() != null && existing.getCreatedAt() != null &&
                    existing.getUpdatedAt().isAfter(existing.getCreatedAt())) {
                    log.debug("Skipping date {} for staff id={} (user-edited)", current, staffId);
                    current = current.plusDays(1);
                    continue;
                }

                // Rule 4b: CRON may update if updatedAt == createdAt (not user-edited)
                if (isClosedDay) {
                    // Update to CLOSED status
                    log.debug("Updating to CLOSED for staff id={} on {}", staffId, current);
                    existing.setStatus(AvailabilityStatus.CLOSED);
                    existing.setStartTime(defaultStartTime);
                    existing.setEndTime(defaultEndTime);
                    existing.setUpdatedAt(LocalDateTime.now());
                    availabilityRepository.save(existing);
                    count++;
                } else {
                    // Update to AVAILABLE with default times
                    log.debug("Updating to AVAILABLE for staff id={} on {}", staffId, current);
                    existing.setStatus(AvailabilityStatus.AVAILABLE);
                    existing.setStartTime(defaultStartTime);
                    existing.setEndTime(defaultEndTime);
                    existing.setUpdatedAt(LocalDateTime.now());
                    availabilityRepository.save(existing);
                    count++;
                }
            } else {
                // Rule 4c: Create new availability
                // Need to create a lightweight Staff reference for the foreign key
                Staff staffRef = staffRepository.getReferenceById(staffId);

                if (isClosedDay) {
                    log.debug("Creating CLOSED availability for staff id={} on {}", staffId, current);
                    StaffAvailability newAvailability = new StaffAvailability();
                    newAvailability.setStaff(staffRef);
                    newAvailability.setDate(current);
                    newAvailability.setStartTime(defaultStartTime);
                    newAvailability.setEndTime(defaultEndTime);
                    newAvailability.setStatus(AvailabilityStatus.CLOSED);
                    newAvailability.setUserEdited(false);
                    availabilityRepository.save(newAvailability);
                    count++;
                } else {
                    log.debug("Creating AVAILABLE availability for staff id={} on {}", staffId, current);
                    StaffAvailability newAvailability = new StaffAvailability();
                    newAvailability.setStaff(staffRef);
                    newAvailability.setDate(current);
                    newAvailability.setStartTime(defaultStartTime);
                    newAvailability.setEndTime(defaultEndTime);
                    newAvailability.setStatus(AvailabilityStatus.AVAILABLE);
                    newAvailability.setUserEdited(false);
                    availabilityRepository.save(newAvailability);
                    count++;
                }
            }

            current = current.plusDays(1);
        }

        log.info("Generated {} availabilities for staff id={}", count, staffId);
        return count;
    }
}
