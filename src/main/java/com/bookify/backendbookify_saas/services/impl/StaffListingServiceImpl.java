package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.UserProfileResponse;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.services.StaffListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation that uses the repository JPQL constructor-expression to return DTOs.
 * This avoids putting JDBC logic in the service and uses the repository query directly.
 */
@Service
@RequiredArgsConstructor
public class StaffListingServiceImpl implements StaffListingService {

    private final BusinessRepository businessRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> listStaffForBusiness(Long businessId) {
        // Validate business exists
        if (!businessRepository.existsById(businessId)) return List.of();

        List<UserProfileResponse> dto = staffRepository.findUserProfileResponsesByBusinessId(businessId);
        if (dto != null && !dto.isEmpty()) return dto;

        // Fallback: native repository method returning raw Object[] rows (u.id, u.name, ...)
        List<Object[]> rows = staffRepository.findUserRowsByBusinessIdNative(businessId);
        if (rows == null || rows.isEmpty()) return List.of();

        // Map Object[] rows to DTOs (native query now includes default_start_time and default_end_time at positions 7/8)
        List<UserProfileResponse> mapped = rows.stream().map(r -> {
            // r[0]=id, r[1]=name, r[2]=email, r[3]=phone_number, r[4]=role, r[5]=status, r[6]=avatar_url, r[7]=default_start_time, r[8]=default_end_time
            Long id = r[0] == null ? null : ((Number) r[0]).longValue();
            String name = r[1] == null ? null : r[1].toString();
            String email = r[2] == null ? null : r[2].toString();
            String phone = r[3] == null ? null : r[3].toString();
            String roleStr = r[4] == null ? null : r[4].toString();
            String statusStr = r[5] == null ? null : r[5].toString();
            String avatar = r[6] == null ? null : r[6].toString();

            java.time.LocalTime start = null;
            java.time.LocalTime end = null;
            try {
                if (r.length > 7 && r[7] != null) {
                    // may be java.sql.Time or String depending on JDBC driver
                    if (r[7] instanceof java.sql.Time) start = ((java.sql.Time) r[7]).toLocalTime();
                    else start = java.time.LocalTime.parse(r[7].toString());
                }
                if (r.length > 8 && r[8] != null) {
                    if (r[8] instanceof java.sql.Time) end = ((java.sql.Time) r[8]).toLocalTime();
                    else end = java.time.LocalTime.parse(r[8].toString());
                }
            } catch (Exception ignored) {
                // ignore parse errors and leave times null
            }

            com.bookify.backendbookify_saas.models.enums.RoleEnum role = null;
            com.bookify.backendbookify_saas.models.enums.UserStatusEnum status = null;
            try { if (roleStr != null) role = com.bookify.backendbookify_saas.models.enums.RoleEnum.valueOf(roleStr); } catch (Exception ignored) {}
            try { if (statusStr != null) status = com.bookify.backendbookify_saas.models.enums.UserStatusEnum.valueOf(statusStr); } catch (Exception ignored) {}

            return UserProfileResponse.builder()
                    .userId(id)
                    .name(name)
                    .email(email)
                    .phoneNumber(phone)
                    .role(role)
                    .status(status)
                    .avatarUrl(avatar)
                    .defaultStartTime(start)
                    .defaultEndTime(end)
                    .build();
        }).toList();

        return mapped;
    }
}
