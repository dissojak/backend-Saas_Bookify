 # All CRON Jobs Summary

## Overview
This document lists all scheduled CRON jobs in the Bookify backend system.

## Active CRON Jobs

### 1. Activation Token Cleanup
- **Class**: `ActivationTokenCleanupScheduler`
- **Schedule**: Daily at 3:00 AM
- **Cron**: `0 0 3 * * ?`
- **Purpose**: Delete expired activation tokens
- **Location**: `src/main/java/com/bookify/backendbookify_saas/schedulers/`

### 2. Staff Availability Generator
- **Class**: `StaffAvailabilityScheduler`
- **Schedule**: Daily at 2:00 AM
- **Cron**: `0 0 2 * * ?`
- **Purpose**: Auto-generate staff availabilities 1 month ahead
- **Manual Endpoint**: `POST /v1/system/cron/generate-staff-availabilities`
- **Documentation**: See `STAFF_AVAILABILITY_GENERATOR.md`
- **Features**:
  - Generates availabilities from today to today + 1 month
  - Skips staff without default working times
  - Sets CLOSED status for Sundays and business weekend_day
  - Sets AVAILABLE status for other days
  - Respects user-edited availabilities

### 3. Weekend Day Sync
- **Class**: `WeekendDaySyncScheduler`
- **Schedule**: Every hour
- **Cron**: `0 0 * * * ?`
- **Purpose**: Sync staff availabilities when business weekend_day changes
- **Manual Endpoint**: `POST /v1/system/cron/sync-weekend-days`
- **Documentation**: See `WEEKEND_DAY_SYNC.md`
- **Features**:
  - Updates old weekend days from CLOSED to AVAILABLE
  - Updates new weekend days from AVAILABLE to CLOSED
  - Respects user-edited availabilities
  - Processes next 1 month of availabilities

## CRON Schedule Timeline

```
Time    | Job
--------|--------------------------------------------------
00:00   | Weekend Day Sync
01:00   | Weekend Day Sync
02:00   | Staff Availability Generator, Weekend Day Sync
03:00   | Activation Token Cleanup, Weekend Day Sync
04:00   | Weekend Day Sync
...     | (Weekend Day Sync continues every hour)
23:00   | Weekend Day Sync
```

## Manual Trigger Endpoints

All CRON jobs can be manually triggered by admins:

### 1. Generate Staff Availabilities
```bash
POST /v1/system/cron/generate-staff-availabilities
Authorization: Bearer <ADMIN_TOKEN>
```

Response:
```json
{
  "message": "Staff availabilities generated successfully",
  "totalGenerated": 450,
  "dateRange": {
    "from": "2025-11-27",
    "to": "2025-12-27"
  }
}
```

### 2. Sync Weekend Days
```bash
POST /v1/system/cron/sync-weekend-days
Authorization: Bearer <ADMIN_TOKEN>
```

Response:
```json
{
  "message": "Weekend days synced successfully",
  "totalUpdated": 45
}
```

## Monitoring

### Log Patterns

#### CRON Start
```
=== CRON: Starting [job name] ===
```

#### CRON Success
```
=== CRON: [job name] completed successfully. Total: X ===
```

#### CRON Error
```
=== CRON: [job name] failed ===
```

### Search Queries

To monitor CRON jobs in logs:
```bash
# All CRON activity
grep "=== CRON:" application.log

# Specific job
grep "Staff availability" application.log
grep "Weekend day sync" application.log
grep "Activation token" application.log

# Errors only
grep "CRON.*failed" application.log
```

## Configuration

### Enable/Disable Scheduling

Scheduling is enabled by default via:
```java
@SpringBootApplication
@EnableScheduling  // This annotation enables all CRON jobs
public class BackendBookifySaasApplication {
    // ...
}
```

To disable all CRON jobs:
```properties
# application.properties
spring.task.scheduling.enabled=false
```

### Timezone

All CRON jobs use the server's default timezone. To set explicitly:
```properties
# application.properties
spring.task.scheduling.timezone=Europe/Paris
```

## Dependencies

### Required Beans
- `StaffAvailabilityGeneratorService` - For availability generation
- `WeekendDaySyncService` - For weekend day sync
- `ActivationTokenRepository` - For token cleanup

### Database Tables
- `staff_availabilities` - Main table for storing availabilities
- `staff` - Staff member data
- `businesses` - Business settings (weekend_day)
- `users` - User activation tokens

## Performance Considerations

### Staff Availability Generator
- Processes all businesses sequentially
- Uses `@Transactional` per business
- Typical execution: 1-5 seconds per business
- Expected total time: 5-30 seconds for small deployments

### Weekend Day Sync
- Processes all businesses sequentially
- Only updates changed availabilities
- Typical execution: 0.5-2 seconds per business
- Expected total time: 2-10 seconds for small deployments

### Activation Token Cleanup
- Single database DELETE query
- Very fast: < 1 second

## Error Handling

All CRON jobs use try-catch blocks:
- Errors for one business don't affect others
- Errors are logged with full stack trace
- Jobs continue to next business on error
- Manual endpoints return 500 with error details

## Testing

### Test Individual CRON Jobs

Use the manual endpoints to test without waiting for schedule:

```bash
# Test availability generator
curl -X POST http://localhost:8088/api/v1/system/cron/generate-staff-availabilities \
  -H "Authorization: Bearer <ADMIN_TOKEN>"

# Test weekend sync
curl -X POST http://localhost:8088/api/v1/system/cron/sync-weekend-days \
  -H "Authorization: Bearer <ADMIN_TOKEN>"
```

### Verify CRON Execution

Check application startup logs:
```
Scheduled task 'staffAvailabilityScheduler.generateDailyAvailabilities' scheduled with cron expression '0 0 2 * * ?'
Scheduled task 'weekendDaySyncScheduler.syncWeekendDays' scheduled with cron expression '0 0 * * * ?'
```

## Troubleshooting

### CRON Not Running

1. Check `@EnableScheduling` is present on main application class
2. Check `spring.task.scheduling.enabled` is not set to false
3. Check bean is created: search logs for scheduler class name
4. Check no exceptions during application startup

### CRON Runs But Fails

1. Check database connection is available
2. Check required services are injected
3. Check logs for specific error messages
4. Use manual endpoint to test with immediate feedback

### Wrong Timing

1. Verify server timezone matches expected timezone
2. Check cron expression is correct
3. Check if daylight saving time affects execution

