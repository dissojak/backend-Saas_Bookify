package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.ActivationToken;
import com.bookify.backendbookify_saas.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository pour l'entité ActivationToken
 */
@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByToken(String token);

    Optional<ActivationToken> findByUser(User user);

    void deleteByExpiryDateBefore(LocalDateTime date);

    void deleteByUser(User user);
}

