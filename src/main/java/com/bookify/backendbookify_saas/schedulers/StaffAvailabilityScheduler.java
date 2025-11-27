package com.bookify.backendbookify_saas.schedulers;

import com.bookify.backendbookify_saas.services.StaffAvailabilityGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Daily CRON job to auto-generate staff availabilities 1 month ahead.
 * Runs once per day at 2:00 AM.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StaffAvailabilityScheduler {

    private final StaffAvailabilityGeneratorService generatorService;

    /**
     * Runs daily at 2:00 AM to generate staff availabilities.
     * Uses cron expression: 0 0 2 * * ? (second minute hour day month weekday)
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateDailyAvailabilities() {
        log.info("=== CRON: Starting daily staff availability generation ===");
        try {
            int count = generatorService.generateForAllBusinesses();
            log.info("=== CRON: Daily generation completed successfully. Total: {} ===", count);
        } catch (Exception ex) {
            log.error("=== CRON: Daily generation failed ===", ex);
        }
    }
}

