package com.bookify.backendbookify_saas.models.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalTime;

@Data
@Builder
public class StaffHoursResponse {
    private Long staffId;
    private LocalTime defaultStartTime;
    private LocalTime defaultEndTime;
}

