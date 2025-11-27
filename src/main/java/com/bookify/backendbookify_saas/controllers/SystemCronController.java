package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.services.StaffAvailabilityGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/**
 * System/Admin endpoints for manual CRON operations
 */
@RestController
@RequestMapping("/v1/system/cron")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "System - CRON", description = "Manual system/admin endpoints for CRON operations")
public class SystemCronController {

    private final StaffAvailabilityGeneratorService generatorService;
    private final com.bookify.backendbookify_saas.services.WeekendDaySyncService weekendDaySyncService;

    /**
     * Manually trigger staff availability generation for all businesses.
     * Runs the same logic as the daily CRON job.
     */
    @PostMapping("/generate-staff-availabilities")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually generate staff availabilities", description = "Admin-only endpoint to manually trigger the staff availability generator (same as daily CRON)")
    public ResponseEntity<Map<String, Object>> generateStaffAvailabilities() {
        log.info("Manual trigger: Generating staff availabilities for all businesses");

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusMonths(1);

        try {
            int count = generatorService.generateForAllBusinesses();

            return ResponseEntity.ok(Map.of(
                "message", "Staff availabilities generated successfully",
                "totalGenerated", count,
                "dateRange", Map.of(
                    "from", today.toString(),
                    "to", endDate.toString()
                )
            ));
        } catch (Exception ex) {
            log.error("Manual generation failed", ex);
            return ResponseEntity.status(500).body(Map.of(
                "message", "Generation failed",
                "error", ex.getMessage()
            ));
        }
    }

    /**
     * Manually trigger weekend day sync for all businesses.
     * Updates staff availabilities to reflect current weekend_day settings.
     */
    @PostMapping("/sync-weekend-days")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Manually sync weekend days", description = "Admin-only endpoint to sync staff availabilities based on current business weekend_day settings")
    public ResponseEntity<Map<String, Object>> syncWeekendDays() {
        log.info("Manual trigger: Syncing weekend days for all businesses");

        try {
            int count = weekendDaySyncService.syncAllBusinesses();

            return ResponseEntity.ok(Map.of(
                "message", "Weekend days synced successfully",
                "totalUpdated", count
            ));
        } catch (Exception ex) {
            log.error("Manual weekend sync failed", ex);
            return ResponseEntity.status(500).body(Map.of(
                "message", "Sync failed",
                "error", ex.getMessage()
            ));
        }
    }
}
