package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.ServiceResponse;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.repositories.ServiceRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import com.bookify.backendbookify_saas.services.StaffPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link com.bookify.backendbookify_saas.services.StaffPublicService}
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class StaffPublicServiceImpl implements StaffPublicService {

    private final ServiceRepository serviceRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceResponse> listServicesForStaff(Long staffId) {
        if (!staffRepository.existsById(staffId)) {
            throw new IllegalArgumentException("Staff not found");
        }
        List<Service> services = serviceRepository.findByStaff_IdAndActiveTrue(staffId);
        return services.stream().map(this::toDto).collect(Collectors.toList());
    }

    private ServiceResponse toDto(Service s) {
        List<Long> staffIds = s.getStaff() == null ? List.of() : s.getStaff().stream().map(u -> u.getId()).toList();
        return ServiceResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .durationMinutes(s.getDurationMinutes())
                .price(s.getPrice())
                .imageUrl(s.getImageUrl())
                .active(s.getActive())
                .staffIds(staffIds)
                .createdAt(s.getCreatedAt())
                .updatedAt(s.getUpdatedAt())
                .build();
    }
}
