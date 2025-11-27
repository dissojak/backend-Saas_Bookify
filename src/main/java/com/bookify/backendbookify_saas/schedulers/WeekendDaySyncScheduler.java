package com.bookify.backendbookify_saas.schedulers;

import com.bookify.backendbookify_saas.services.WeekendDaySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * CRON job to sync staff availabilities when business weekend_day changes.
 * Runs every hour to ensure weekend day changes are reflected in availabilities.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeekendDaySyncScheduler {

    private final WeekendDaySyncService weekendDaySyncService;

    /**
     * Runs every hour to sync weekend days with staff availabilities.
     * Uses cron expression: 0 0 * * * ? (runs at the top of every hour)
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void syncWeekendDays() {
        log.info("=== CRON: Starting weekend day sync ===");
        try {
            int count = weekendDaySyncService.syncAllBusinesses();
            log.info("=== CRON: Weekend day sync completed. Total updated: {} ===", count);
        } catch (Exception ex) {
            log.error("=== CRON: Weekend day sync failed ===", ex);
        }
    }
}

