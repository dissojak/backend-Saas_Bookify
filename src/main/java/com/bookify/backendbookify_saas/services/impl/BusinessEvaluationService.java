package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessEvaluation;
import com.bookify.backendbookify_saas.repositories.BusinessEvaluationRepository;
import com.bookify.backendbookify_saas.services.BusinessProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class BusinessEvaluationService {

    private final BusinessEvaluationRepository evaluationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key:}")
    private String openAiKey;

    @Value("${openai.api.model:gpt-5}")
    private String openAiModel;

    @Value("${openai.api.temperature:0.5}")
    private double temperature;

    @Value("${openai.api.max-tokens:3000}")
    private int maxTokens;

    @Transactional
    public BusinessEvaluation evaluateAndSave(Business business) {
        BusinessProfile profile = new BusinessProfileAdapter(business);
        EvaluationResult res = null;

        if (openAiKey != null && !openAiKey.isBlank()) {
            try {
                res = aiEvaluate(profile);
            } catch (Exception ignored) {}
        }

        if (res == null) {
            res = aiHeuristic(profile);
        }

        BusinessEvaluation eval = BusinessEvaluation.builder()
                .business(business)
                .brandingScore(res.brandingScore)
                .nameProfessionalismScore(res.nameScore)
                .emailProfessionalismScore(res.emailScore)
                .descriptionProfessionalismScore(res.descriptionScore)
                .overallScore(res.overall)
                .nameDetails(res.nameDetails)
                .emailDetails(res.emailDetails)
                .descriptionDetails(res.descriptionDetails)
                .brandingDetails(res.brandingDetails)
                .nameSuggestions(res.nameSuggestions)
                .emailSuggestions(res.emailSuggestions)
                .descriptionSuggestions(res.descriptionSuggestions)
                .brandingSuggestions(res.brandingSuggestions)
                .source(res.source)
                .build();

        return evaluationRepository.save(eval);
    }

    private EvaluationResult aiEvaluate(BusinessProfile p) throws Exception {
        String prompt = """
            You are a business evaluation expert.
            Evaluate the professionalism of the following business and return ONLY a valid JSON of the form:
            {
              "branding": { "score": int, "details": string, "suggestions": string },
              "name": { "score": int, "details": string, "suggestions": string },
              "email": { "score": int, "details": string, "suggestions": string },
              "description": { "score": int, "details": string, "suggestions": string },
              "overall": int
            }
            Constraints:
            - Scores must be integers between 0 and 100.
            - No extra text or explanation outside the JSON.
            Business data:
            """ + String.format("""
            Name: %s
            Location: %s
            Category: %s
            Phone: %s
            Email: %s
            Description: %s
            """,
                nz(p.getName()), nz(p.getLocation()), nz(p.getCategoryName()), nz(p.getPhone()), nz(p.getEmail()), nz(p.getDescription())
        );

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "model", openAiModel,
                "temperature", temperature,
                "max_tokens", maxTokens,
                "messages", new Object[]{
                        java.util.Map.of("role", "system", "content", "You must return only a valid JSON."),
                        java.util.Map.of("role", "user", "content", prompt)
                }
        ));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + openAiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) return null;

        String content = objectMapper.readTree(resp.body())
                .path("choices").path(0).path("message").path("content").asText("");
        if (content.isBlank()) return null;

        String cleaned = content.trim();
        if (cleaned.startsWith("```")) {
            int idx = cleaned.indexOf('{');
            int last = cleaned.lastIndexOf('}');
            if (idx >= 0 && last > idx) cleaned = cleaned.substring(idx, last + 1);
        }

        JsonNode json = objectMapper.readTree(cleaned);
        EvaluationResult r = new EvaluationResult();
        r.source = "AI";
        r.brandingScore = clamp(json.path("branding").path("score").asInt(50));
        r.brandingDetails = safeText(json.path("branding"), "details");
        r.brandingSuggestions = safeText(json.path("branding"), "suggestions");
        r.nameScore = clamp(json.path("name").path("score").asInt(50));
        r.nameDetails = safeText(json.path("name"), "details");
        r.nameSuggestions = safeText(json.path("name"), "suggestions");
        r.emailScore = clamp(json.path("email").path("score").asInt(50));
        r.emailDetails = safeText(json.path("email"), "details");
        r.emailSuggestions = safeText(json.path("email"), "suggestions");
        r.descriptionScore = clamp(json.path("description").path("score").asInt(50));
        r.descriptionDetails = safeText(json.path("description"), "details");
        r.descriptionSuggestions = safeText(json.path("description"), "suggestions");
        r.overall = clamp(json.path("overall").asInt(
                (r.brandingScore + r.nameScore + r.emailScore + r.descriptionScore) / 4));
        return r;
    }

    private EvaluationResult aiHeuristic(BusinessProfile p) {
        try {
            String fallbackPrompt = """
                You are an assistant providing fallback evaluation for a business profile.
                Based on the following data, generate realistic scores and short details + suggestions for each field (branding, name, email, description).
                Return ONLY valid JSON with the same structure as the AI evaluation.
                """ + String.format("""
                Name: %s
                Location: %s
                Category: %s
                Phone: %s
                Email: %s
                Description: %s
                """,
                    nz(p.getName()), nz(p.getLocation()), nz(p.getCategoryName()), nz(p.getPhone()), nz(p.getEmail()), nz(p.getDescription())
            );
            return aiEvaluatePrompt(fallbackPrompt, "AI-Fallback");
        } catch (Exception e) {
            EvaluationResult r = new EvaluationResult();
            r.source = "HEURISTIC";
            r.brandingScore = 50;
            r.nameScore = 50;
            r.emailScore = 50;
            r.descriptionScore = 50;
            r.overall = 50;
            r.brandingDetails = "Basic fallback evaluation.";
            r.nameDetails = "Basic fallback evaluation.";
            r.emailDetails = "Basic fallback evaluation.";
            r.descriptionDetails = "Basic fallback evaluation.";
            r.brandingSuggestions = "No suggestions provided.";
            r.nameSuggestions = "No suggestions provided.";
            r.emailSuggestions = "No suggestions provided.";
            r.descriptionSuggestions = "No suggestions provided.";
            return r;
        }
    }

    private EvaluationResult aiEvaluatePrompt(String prompt, String sourceLabel) throws Exception {
        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "model", openAiModel,
                "temperature", temperature,
                "max_tokens", maxTokens,
                "messages", new Object[]{
                        java.util.Map.of("role", "system", "content", "You must return only a valid JSON."),
                        java.util.Map.of("role", "user", "content", prompt)
                }
        ));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + openAiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(20))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        String content = objectMapper.readTree(resp.body())
                .path("choices").path(0).path("message").path("content").asText("");
        JsonNode json = objectMapper.readTree(content);

        EvaluationResult r = new EvaluationResult();
        r.source = sourceLabel;
        r.brandingScore = clamp(json.path("branding").path("score").asInt(50));
        r.nameScore = clamp(json.path("name").path("score").asInt(50));
        r.emailScore = clamp(json.path("email").path("score").asInt(50));
        r.descriptionScore = clamp(json.path("description").path("score").asInt(50));
        r.overall = clamp(json.path("overall").asInt(
                (r.brandingScore + r.nameScore + r.emailScore + r.descriptionScore) / 4));
        r.brandingDetails = safeText(json.path("branding"), "details");
        r.brandingSuggestions = safeText(json.path("branding"), "suggestions");
        r.nameDetails = safeText(json.path("name"), "details");
        r.nameSuggestions = safeText(json.path("name"), "suggestions");
        r.emailDetails = safeText(json.path("email"), "details");
        r.emailSuggestions = safeText(json.path("email"), "suggestions");
        r.descriptionDetails = safeText(json.path("description"), "details");
        r.descriptionSuggestions = safeText(json.path("description"), "suggestions");
        return r;
    }

    private static int clamp(int v) { return Math.max(0, Math.min(100, v)); }

    private static String nz(String s) { return s == null ? "" : s; }

    private static String safeText(JsonNode node, String field) {
        String value = node.path(field).asText("");
        return (value == null || value.isBlank()) ? "No suggestions provided." : value.trim();
    }

    private static class EvaluationResult {
        int brandingScore;
        int nameScore;
        int emailScore;
        int descriptionScore;
        int overall;
        String brandingDetails;
        String brandingSuggestions;
        String nameDetails;
        String nameSuggestions;
        String emailDetails;
        String emailSuggestions;
        String descriptionDetails;
        String descriptionSuggestions;
        String source;
    }
}
