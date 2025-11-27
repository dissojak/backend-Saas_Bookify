package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.StaffAvailability;
import com.bookify.backendbookify_saas.models.enums.AvailabilityStatus;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.StaffAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service to sync staff availabilities when business weekend_day changes.
 * Ensures that old weekend days are set to AVAILABLE and new weekend days are set to CLOSED.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WeekendDaySyncService {

    private final BusinessRepository businessRepository;
    private final StaffAvailabilityRepository availabilityRepository;

    /**
     * Sync all businesses' staff availabilities based on current weekend_day settings.
     * Called by CRON job to ensure consistency.
     */
    @Transactional
    public int syncAllBusinesses() {
        log.info("Starting weekend day sync for all businesses");
        List<Business> businesses = businessRepository.findAll();
        log.info("Found {} businesses in database", businesses.size());

        if (businesses.isEmpty()) {
            log.warn("No businesses found in database!");
            return 0;
        }

        // Log all business IDs being processed
        businesses.forEach(b -> log.info("Will process business id={} name={}", b.getId(), b.getName()));

        int totalUpdated = 0;

        for (Business business : businesses) {
            try {
                int count = syncBusinessWeekendDays(business.getId());
                totalUpdated += count;
                log.info("Synced {} availabilities for business id={}", count, business.getId());
            } catch (Exception ex) {
                log.error("Failed to sync weekend days for business id={}: {}", business.getId(), ex.getMessage(), ex);
            }
        }

        log.info("Weekend day sync completed. Total availabilities updated: {}", totalUpdated);
        return totalUpdated;
    }

    /**
     * Sync staff availabilities for a specific business based on its current weekend_day.
     * This handles:
     * 1. Old weekend days that should now be AVAILABLE (if weekend_day changed)
     * 2. New weekend days that should now be CLOSED
     * 3. Sundays are always CLOSED (special rule)
     *
     * @param businessId The business ID
     * @return Number of availabilities updated
     */
    @Transactional
    public int syncBusinessWeekendDays(Long businessId) {
        log.info("Syncing weekend days for business id={}", businessId);

        Business business = businessRepository.findById(businessId)
                .orElseThrow(() -> new IllegalArgumentException("Business not found: " + businessId));

        DayOfWeek currentWeekendDay = business.getWeekendDay();
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(1);

        log.info("Business id={} name={} weekendDay={}", businessId, business.getName(), currentWeekendDay);

        int updatedCount = 0;
        int totalAvailabilitiesFound = 0;

        // Fetch all availabilities for the whole range in a single query
        List<StaffAvailability> allInRange = availabilityRepository.findByBusinessIdAndDateRange(businessId, today, endDate);
        log.info("Fetched {} availabilities for business id={} in range {}..{}", allInRange.size(), businessId, today, endDate);

        // Use bulk native updates to apply changes in the DB (faster and avoids JPA state issues)
        // Map Java DayOfWeek to MySQL DAYOFWEEK: MySQL 1=Sunday,2=Monday,... Java MONDAY=1 -> MySQL=2 => mysqlDow = (javaValue % 7) + 1
        int mysqlDow = (currentWeekendDay == null) ? 0 : (currentWeekendDay.getValue() % 7) + 1;
        // closed: set CLOSED for dates that are Sunday (1) OR the business weekend day (mysqlDow)
        int closedUpdated = availabilityRepository.bulkUpdateStatusToClosedForBusinessInRange(businessId, today, endDate, mysqlDow, AvailabilityStatus.CLOSED.name());
        // available: set AVAILABLE for dates that are not Sunday and not the business weekend day
        int availableUpdated = availabilityRepository.bulkUpdateStatusToAvailableForBusinessInRange(businessId, today, endDate, mysqlDow, AvailabilityStatus.AVAILABLE.name());

        updatedCount = closedUpdated + availableUpdated;
        totalAvailabilitiesFound = allInRange.size();
        log.info("Bulk updates applied: closedUpdated={}, availableUpdated={}", closedUpdated, availableUpdated);

        log.info("Total availabilities found: {} for business id={}", totalAvailabilitiesFound, businessId);
        log.info("Synced {} availabilities for business id={} name={}", updatedCount, businessId, business.getName());

        if (totalAvailabilitiesFound == 0) {
            log.warn("No availabilities found for business id={}. You may need to run the availability generator first.", businessId);
        }

        return updatedCount;
    }
}
