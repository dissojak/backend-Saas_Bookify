# Weekend Day Sync Feature

## Overview
Automatically syncs staff availabilities when business owners change the `weekend_day` setting. This ensures that:
- Old weekend days are updated from CLOSED to AVAILABLE
- New weekend days are updated from AVAILABLE to CLOSED
- Sundays always remain CLOSED (special rule)

## Components

### 1. WeekendDaySyncService
- **Class**: `WeekendDaySyncService`
- **Method**: `syncAllBusinesses()` - Syncs all businesses
- **Method**: `syncBusinessWeekendDays(businessId)` - Syncs a specific business
- **Logic**:
  - Processes all dates from today to today + 1 month
  - For each date, determines if it should be CLOSED (Sunday OR matches weekend_day)
  - Updates availability status accordingly
  - Skips user-edited availabilities (updatedAt > createdAt)

### 2. CRON Scheduler
- **Class**: `WeekendDaySyncScheduler`
- **Schedule**: Runs every hour at the top of the hour
- **Cron Expression**: `0 0 * * * ?`
- **Purpose**: Automatically detect and sync weekend day changes

### 3. Manual Trigger Endpoint
- **URL**: `POST /v1/system/cron/sync-weekend-days`
- **Auth**: Admin only (`ROLE_ADMIN`)
- **Response**: Returns count of updated availabilities

## Use Cases

### Case 1: Owner Changes Weekend Day
**Before**: Business weekend_day = SATURDAY
**After**: Business weekend_day = FRIDAY

**Result**:
- All SATURDAY availabilities: CLOSED → AVAILABLE
- All FRIDAY availabilities: AVAILABLE → CLOSED
- SUNDAY availabilities: Remain CLOSED

### Case 2: Owner Sets Weekend Day (from null)
**Before**: Business weekend_day = null (no custom weekend)
**After**: Business weekend_day = MONDAY

**Result**:
- All MONDAY availabilities: AVAILABLE → CLOSED
- SUNDAY availabilities: Remain CLOSED

### Case 3: Owner Removes Weekend Day
**Before**: Business weekend_day = THURSDAY
**After**: Business weekend_day = null

**Result**:
- All THURSDAY availabilities: CLOSED → AVAILABLE
- SUNDAY availabilities: Remain CLOSED

## Rules & Behavior

### 1. Status Determination
```
Status = CLOSED if:
  - Day is SUNDAY (always)
  OR
  - Day matches business.weekend_day (if set)
Otherwise:
  - Status = AVAILABLE
```

### 2. User-Edited Protection
- Availabilities where `updatedAt > createdAt` are **NOT** modified
- This preserves manual changes made by staff (e.g., taking a day off)

### 3. Date Range
- Syncs availabilities from today to today + 1 month
- Matches the range used by the daily availability generator

### 4. Idempotency
- Safe to run multiple times
- Only updates when status actually needs to change

## API Examples

### Manual Sync Trigger
```bash
POST http://localhost:8088/api/v1/system/cron/sync-weekend-days
Authorization: Bearer <ADMIN_TOKEN>
```

### Success Response
```json
{
  "message": "Weekend days synced successfully",
  "totalUpdated": 45
}
```

### Error Response
```json
{
  "message": "Sync failed",
  "error": "Error details here"
}
```

## CRON Schedule

### WeekendDaySyncScheduler
- **Frequency**: Every hour
- **Cron**: `0 0 * * * ?`
- **Runs at**: 00:00, 01:00, 02:00, ..., 23:00

### StaffAvailabilityScheduler
- **Frequency**: Once per day
- **Cron**: `0 0 2 * * ?`
- **Runs at**: 02:00 AM

## Logs to Monitor

### Success
```
=== CRON: Starting weekend day sync ===
Syncing weekend days for business id=1
Updated availability id=123 from AVAILABLE to CLOSED for date 2025-11-30
Synced 45 availabilities for business id=1 name=Stoon Barber Shop
=== CRON: Weekend day sync completed. Total updated: 45 ===
```

### Skipped (User-Edited)
```
Skipping user-edited availability id=456 for date 2025-12-01
```

### Error
```
Failed to sync weekend days for business id=2: Business not found: 2
```

## Performance Notes

- Processes each business independently
- Uses `@Transactional` at business level
- Errors for one business don't affect others
- Efficient: Only updates when status actually changes

## Integration with Daily Generator

The weekend day sync works alongside the daily availability generator:

1. **Daily Generator** (02:00 AM):
   - Creates new availabilities for the next month
   - Sets correct CLOSED/AVAILABLE status based on current weekend_day

2. **Weekend Sync** (Every hour):
   - Fixes any existing availabilities that don't match current weekend_day
   - Handles mid-day changes by business owners

Together, these ensure availabilities are always consistent with business settings.

## Future Enhancements

1. Add webhook/event-based triggering when weekend_day changes
2. Add notification to staff when their schedule changes
3. Add audit log for weekend day changes
4. Add rollback capability for accidental changes

