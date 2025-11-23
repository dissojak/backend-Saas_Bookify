package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.services.impl.BusinessEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/health")
public class AiHealthController {

    private final BusinessEvaluationService evaluationService;

    @Autowired
    public AiHealthController(@Qualifier("businessEvaluationService") BusinessEvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("/ai")
    public ResponseEntity<Map<String, Object>> ai() {
        Map<String, Object> health = evaluationService.aiHealth();

        boolean enabled = Boolean.TRUE.equals(health.get("enabled"));
        boolean ok = Boolean.TRUE.equals(health.get("ok"));

        if (!enabled) {
            String reason = health.getOrDefault("reason", "AI not configured").toString();
            return ResponseEntity.status(503).body(Map.of(
                    "ok", false,
                    "message", "AI not configured",
                    "detail", reason
            ));
        }

        if (!ok) {
            String reason = health.getOrDefault("reason", "").toString();
            String errorBody = health.getOrDefault("errorBodyPreview", "").toString();
            String exception = health.getOrDefault("exception", "").toString();
            String combined = (reason + " " + errorBody + " " + exception).toLowerCase();

            // Detect quota/tokens/limits related failures
            if (combined.contains("quota") || combined.contains("exceed") || combined.contains("insufficient")
                    || combined.contains("tokens") || combined.contains("resource_exhausted") || combined.contains("quotaexceeded")) {
                return ResponseEntity.status(429).body(Map.of(
                        "ok", false,
                        "message", "AI quota/tokens exhausted. Please check your billing or API key usage.",
                        "detail", combined.trim()
                ));
            }

            // Generic AI failure
            return ResponseEntity.status(502).body(Map.of(
                    "ok", false,
                    "message", "AI health check failed",
                    "detail", combined.trim(),
                    "raw", health
            ));
        }

        // AI enabled and OK
        return ResponseEntity.ok(Map.of(
                "ok", true,
                "message", "AI reachable",
                "model", health.get("model"),
                "status", health.get("status"),
                "contentPreview", health.get("contentPreview")
        ));
    }
}
