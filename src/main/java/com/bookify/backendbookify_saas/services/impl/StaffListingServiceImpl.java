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

        // Map Object[] rows to DTOs
        List<UserProfileResponse> mapped = rows.stream().map(r -> {
            // r[0]=id, r[1]=name, r[2]=email, r[3]=phone_number, r[4]=role, r[5]=status, r[6]=avatar_url
            Long id = r[0] == null ? null : ((Number) r[0]).longValue();
            String name = r[1] == null ? null : r[1].toString();
            String email = r[2] == null ? null : r[2].toString();
            String phone = r[3] == null ? null : r[3].toString();
            String roleStr = r[4] == null ? null : r[4].toString();
            String statusStr = r[5] == null ? null : r[5].toString();
            String avatar = r[6] == null ? null : r[6].toString();

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
                    .build();
        }).toList();

        return mapped;
    }
}
