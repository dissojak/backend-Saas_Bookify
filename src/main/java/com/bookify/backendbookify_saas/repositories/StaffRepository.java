package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    // Retourne l'id du business si l'utilisateur est déjà staff, sinon vide
    @Query(value = "SELECT business_id FROM staff WHERE id = :id", nativeQuery = true)
    Optional<Long> findBusinessIdById(@Param("id") Long id);

    // Alternative that returns Integer to avoid native-query numeric mapping issues
    @Query(value = "SELECT business_id FROM staff WHERE id = :id", nativeQuery = true)
    Optional<Integer> findBusinessIdIntById(@Param("id") Long id);

    // Check existence of staff membership directly (native) -> renamed to 'count...' to avoid Spring Data "existsBy" boolean expectation
    @Query(value = "SELECT COUNT(1) FROM staff WHERE id = :id AND business_id = :businessId", nativeQuery = true)
    int countByIdAndBusinessIdRaw(@Param("id") Long id, @Param("businessId") Long businessId);

    // JPQL alternative: safer and avoids native flush/crosstalk issues with entity mappings
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.id = :id AND s.business.id = :businessId")
    int countByIdAndBusinessId(@Param("id") Long id, @Param("businessId") Long businessId);

    // Spring Data derived query returning boolean — simplest and safest to check membership
    boolean existsByIdAndBusiness_Id(Long id, Long businessId);

    // Fetch all staff members that belong to a business (used by public listing endpoint)
    List<Staff> findByBusiness_Id(Long businessId);

    // Explicit JPQL version to avoid ambiguity with inherited User.business property
    @Query("SELECT s FROM Staff s JOIN FETCH s.business b WHERE b.id = :businessId")
    List<Staff> findStaffByBusinessId(@Param("businessId") Long businessId);

    // Simple JPQL that selects Staff by business id (no fetch) - use this as the primary query
    @Query("SELECT s FROM Staff s WHERE s.business.id = :businessId")
    List<Staff> findStaffByBusinessIdSimple(@Param("businessId") Long businessId);

    // Return DTOs directly using JPQL constructor expression to avoid manual mapping in controllers
    @Query("SELECT new com.bookify.backendbookify_saas.models.dtos.UserProfileResponse(s.id, s.name, s.email, s.phoneNumber, s.role, s.status, s.avatarUrl, null, null, null) " +
           "FROM Staff s WHERE s.business.id = :businessId")
    List<com.bookify.backendbookify_saas.models.dtos.UserProfileResponse> findUserProfileResponsesByBusinessId(@Param("businessId") Long businessId);

    // Native query fallback: select staff table rows by business_id to avoid JPA property-name collisions
    @Query(value = "SELECT s.* FROM staff s WHERE s.business_id = :businessId", nativeQuery = true)
    List<Staff> findStaffByBusinessIdNative(@Param("businessId") Long businessId);

    // Native query returning user fields joined with staff; returns Object[] rows as fallback for DTO mapping
    @Query(value = "SELECT u.id, u.name, u.email, u.phone_number, u.role, u.status, u.avatar_url " +
            "FROM users u JOIN staff s ON u.id = s.id WHERE s.business_id = :businessId", nativeQuery = true)
    List<Object[]> findUserRowsByBusinessIdNative(@Param("businessId") Long businessId);

    // Met à jour le rôle de l'utilisateur dans la table users (native SQL pour éviter flush / persister issues)
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE users SET role = :role WHERE id = :id", nativeQuery = true)
    int updateUserRole(@Param("id") Long id, @Param("role") String role);

    // Insère la ligne dans la table staff (native SQL)
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO staff (id, business_id, start_working_at) VALUES (:id, :businessId, CURRENT_DATE)", nativeQuery = true)
    int insertStaffRow(@Param("id") Long id, @Param("businessId") Long businessId);

    // Insert row into service_staff join table (native)
    @Modifying(clearAutomatically = true)
    @Query(value = "INSERT INTO service_staff (service_id, staff_id) VALUES (:serviceId, :staffId)", nativeQuery = true)
    int insertServiceStaffRow(@Param("serviceId") Long serviceId, @Param("staffId") Long staffId);

    // New: check if service_staff row exists to avoid duplicate insert
    @Query(value = "SELECT COUNT(1) FROM service_staff WHERE service_id = :serviceId AND staff_id = :staffId", nativeQuery = true)
    int countServiceStaffRow(@Param("serviceId") Long serviceId, @Param("staffId") Long staffId);
}
