package com.bookify.backendbookify_saas.config;

import com.bookify.backendbookify_saas.models.entities.listeners.BusinessEntityListener;
import com.bookify.backendbookify_saas.services.impl.BusinessEvaluationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BusinessEntityListenerConfig {

    private final BusinessEvaluationService evaluationService;

    @PostConstruct
    public void wireListener() {
        BusinessEntityListener.setEvaluationService(evaluationService);
    }
}

