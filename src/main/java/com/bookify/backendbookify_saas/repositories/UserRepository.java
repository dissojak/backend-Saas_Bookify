package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;
import com.bookify.backendbookify_saas.models.enums.UserStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for the User entity
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(RoleEnum role);

    @Query("SELECT u.id FROM User u WHERE u.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    @Query("""
        SELECT u FROM User u
        WHERE (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))
          AND (:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')))
          AND (:role IS NULL OR u.role = :role)
          AND (:status IS NULL OR u.status = :status)
          AND (:fromDate IS NULL OR u.createdAt >= :fromDate)
          AND (:toDate IS NULL OR u.createdAt <= :toDate)
        ORDER BY u.createdAt DESC
    """)
    List<User> searchUsers(
            @Param("email") String email,
            @Param("name") String name,
            @Param("role") RoleEnum role,
            @Param("status") UserStatusEnum status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
}
