package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}


