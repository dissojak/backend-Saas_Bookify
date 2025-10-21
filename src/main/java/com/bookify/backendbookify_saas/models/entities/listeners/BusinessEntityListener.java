package com.bookify.backendbookify_saas.models.entities.listeners;

import com.bookify.backendbookify_saas.models.entities.Business;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

public class BusinessEntityListener {

    @PostPersist
    public void afterCreate(Business business) {
        // Evaluation creation is handled by the service layer after business is fully persisted
        // This prevents TransientPropertyValueException
    }

    @PostUpdate
    public void afterUpdate(Business business) {
        // Evaluation creation is handled by the service layer after business is fully persisted
        // This prevents TransientPropertyValueException
    }
}
