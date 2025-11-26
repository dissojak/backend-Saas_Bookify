package com.bookify.backendbookify_saas.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO to update staff default start/end times. Times are ISO_LOCAL_TIME strings (e.g. "09:00").
 */
@Getter
@Setter
@NoArgsConstructor
public class StaffHoursUpdateRequest {
    private String defaultStartTime; // e.g. "09:00"
    private String defaultEndTime;   // e.g. "17:30"
}

