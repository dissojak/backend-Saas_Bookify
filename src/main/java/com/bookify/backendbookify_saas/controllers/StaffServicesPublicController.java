package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.ServiceResponse;
import com.bookify.backendbookify_saas.services.StaffPublicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/staff")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public - Staff Services", description = "Public endpoints to list services linked to a staff")
public class StaffServicesPublicController {

    private final StaffPublicService staffPublicService;

    @GetMapping("/{staffId}/services")
    @Operation(summary = "List services for staff", description = "Public route that lists active services linked to a staff member")
    public ResponseEntity<List<ServiceResponse>> listServicesForStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(staffPublicService.listServicesForStaff(staffId));
    }
}
