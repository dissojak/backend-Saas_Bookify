package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.models.enums.RoleEnum;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user management
 */
public interface UserService {

    User createUser(User user);

    User updateUser(Long id, User user);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByEmail(String email);

    List<User> getAllUsers();

    List<User> getUsersByRole(RoleEnum role);

    void deleteUser(Long id);

    boolean existsByEmail(String email);
}
