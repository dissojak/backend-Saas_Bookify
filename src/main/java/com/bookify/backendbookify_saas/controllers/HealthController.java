package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.services.impl.BusinessEvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/health")
public class HealthController {

    private final BusinessEvaluationService evaluationService;

    @GetMapping("/ai")
    public ResponseEntity<Map<String, Object>> ai() {
        return ResponseEntity.ok(evaluationService.aiHealth());
    }
}

