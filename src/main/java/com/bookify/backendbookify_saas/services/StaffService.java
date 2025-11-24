package com.bookify.backendbookify_saas.services;

/**
 * Interface du service Staff.
 * Définit les opérations liées à la création d'un enregistrement "staff" et la mise à jour du rôle.
 */
public interface StaffService {
    void createStaffRecord(Long userId, Long businessId);
    void createStaffAndSetRole(Long userId, Long businessId);
}
