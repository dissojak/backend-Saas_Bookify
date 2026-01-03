package com.bookify.backendbookify_saas.services;

import java.time.LocalTime;

/**
 * Interface du service Staff.
 * Définit les opérations liées à la création d'un enregistrement "staff" et la mise à jour du rôle.
 */
public interface StaffService {
    void createStaffRecord(Long userId, Long businessId);
    void createStaffAndSetRole(Long userId, Long businessId);
    
    /**
     * Create staff with work hours and auto-generate availabilities.
     * @return the new staff's ID
     */
    Long createStaffAndSetRoleWithWorkHours(Long userId, Long businessId, LocalTime startTime, LocalTime endTime);
}
