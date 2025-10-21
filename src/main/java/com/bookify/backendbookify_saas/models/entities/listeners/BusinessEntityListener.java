package com.bookify.backendbookify_saas.models.entities.listeners;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.services.impl.BusinessEvaluationService;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

public class BusinessEntityListener {

    private static BusinessEvaluationService evaluationService;

    public static void setEvaluationService(BusinessEvaluationService svc) {
        evaluationService = svc;
    }

    @PostPersist
    public void afterCreate(Business business) {
        if (evaluationService != null) {
            evaluationService.evaluateAndSave(business);
        }
    }

    @PostUpdate
    public void afterUpdate(Business business) {
        if (evaluationService != null) {
            evaluationService.evaluateAndSave(business);
        }
    }
}
