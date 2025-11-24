package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.ServiceCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.Service;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.repositories.BusinessRepository;
import com.bookify.backendbookify_saas.repositories.ServiceRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.repositories.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class ServiceCreationService {

    private final ServiceRepository serviceRepository;
    private final BusinessRepository businessRepository;
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Create the Service row in a new transaction without attaching staff entities.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Service createService(Long businessId, ServiceCreateRequest req, Long actorId) {
        Business business = businessRepository.getReferenceById(businessId);
        User actor = userRepository.getReferenceById(actorId);

        Service s = new Service();
        s.setBusiness(business);
        s.setName(req.getName());
        s.setDescription(req.getDescription());
        s.setDurationMinutes(req.getDurationMinutes());
        s.setPrice(req.getPrice());
        s.setImageUrl(req.getImageUrl());
        s.setActive(true);
        s.setCreatedBy(actor);

        Service saved = serviceRepository.save(s);
        log.info("ServiceCreationService: created service id={} in new transaction", saved.getId());
        return saved;
    }

    /**
     * Insert rows into service_staff using repository native queries; if repository calls
     * trigger Hibernate flush issues, fallback to a native insert via EntityManager.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addStaffToService(Long serviceId, List<Long> staffIds) {
        if (staffIds == null || staffIds.isEmpty()) return;
        for (Long staffId : staffIds) {
            try {
                int exists;
                try {
                    exists = staffRepository.countServiceStaffRow(serviceId, staffId);
                } catch (Exception e) {
                    // Count via repository caused an unexpected Hibernate flush issue; log and fallthrough
                    log.warn("addStaffToService: countServiceStaffRow failed for serviceId={}, staffId={} -> {}", serviceId, staffId, e.toString());
                    exists = 0; // assume not exists and try insert via repository first
                }

                if (exists > 0) {
                    log.info("addStaffToService: skip insert because join already exists for serviceId={}, staffId={}", serviceId, staffId);
                    continue;
                }

                try {
                    staffRepository.insertServiceStaffRow(serviceId, staffId);
                    log.info("addStaffToService: inserted join serviceId={}, staffId={} via repository", serviceId, staffId);
                } catch (Exception repoInsertEx) {
                    // Repository insert failed (possibly due to flush/mapping). Try native insert via EntityManager
                    log.warn("addStaffToService: repository insert failed for serviceId={}, staffId={} -> {}, falling back to native insert", serviceId, staffId, repoInsertEx.toString());
                    try {
                        // Use a generic INSERT; DB-specific clauses (IGNORE/ON CONFLICT) are not assumed here.
                        // If the DB supports INSERT IGNORE (MySQL) or ON CONFLICT (Postgres), you can replace accordingly.
                        entityManager.createNativeQuery("INSERT INTO service_staff (service_id, staff_id) VALUES (:serviceId, :staffId)")
                                .setParameter("serviceId", serviceId)
                                .setParameter("staffId", staffId)
                                .executeUpdate();

                        log.info("addStaffToService: inserted join serviceId={}, staffId={} via native EntityManager", serviceId, staffId);
                    } catch (Exception nativeEx) {
                        // Likely duplicate key or FK violation; log and continue
                        log.error("addStaffToService: native insert also failed for serviceId={}, staffId={}", serviceId, staffId, nativeEx);
                    }
                }

            } catch (DataIntegrityViolationException dive) {
                log.warn("addStaffToService: DataIntegrityViolation for serviceId={}, staffId={} -> {}", serviceId, staffId, dive.getMessage());
            } catch (Exception ex) {
                log.error("addStaffToService: failed to ensure service_staff row for serviceId={}, staffId={}", serviceId, staffId, ex);
            }
        }
    }
}
