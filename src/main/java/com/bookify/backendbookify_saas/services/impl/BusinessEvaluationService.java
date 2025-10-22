package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.entities.Business;
import com.bookify.backendbookify_saas.models.entities.BusinessEvaluation;
import com.bookify.backendbookify_saas.repositories.BusinessEvaluationRepository;
import com.bookify.backendbookify_saas.services.BusinessProfile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessEvaluationService {

    private final BusinessEvaluationRepository evaluationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.api.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${gemini.api.temperature:0.7}")
    private double temperature;

    @Value("${gemini.api.max-tokens:1000}")
    private int maxTokens;

    private boolean aiEnabled;

    // Weights for overall score (equal weights across 6 dimensions)
    private static final double W_NAME = 1.0 / 6.0;
    private static final double W_EMAIL = 1.0 / 6.0;
    private static final double W_DESC = 1.0 / 6.0;
    private static final double W_LOC  = 1.0 / 6.0;
    private static final double W_CAT  = 1.0 / 6.0;
    private static final double W_BRAND= 1.0 / 6.0;

    // Heuristic helpers (used when AI disabled or as guardrails)
    private static final Set<String> GENERIC_EMAIL_DOMAINS = Set.of(
            "gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "icloud.com", "live.com", "aol.com"
    );
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern POSTAL_REGEX = Pattern.compile("\\b\\d{4,5}\\b");
    private static final Pattern DIGITS_REGEX = Pattern.compile(".*\\d+.*");
    private static final Pattern BAD_NAME_CHARS = Pattern.compile("[^A-Za-zÀ-ÖØ-öø-ÿ\n\r \t&'\\-.]");

    @PostConstruct
    void initAiFlag() {
        aiEnabled = geminiApiKey != null && !geminiApiKey.isBlank();
        if (!aiEnabled) {
            log.info("Gemini AI is not configured; evaluations will use fallback heuristics.");
        } else {
            log.info("Gemini AI is configured; evaluations will use model '{}'", geminiModel);
        }
    }

    @Transactional
    public BusinessEvaluation evaluateAndSave(Business business) {
        BusinessProfile profile = new BusinessProfileAdapter(business);
        EvaluationResult res = null;

        if (aiEnabled) {
            log.info("Attempting Gemini AI evaluation for business: {}", business.getName());
            try {
                res = aiEvaluateAll(profile);
                log.info("Gemini AI evaluation succeeded for business: {}", business.getName());
            } catch (Exception e) {
                log.warn("Gemini AI evaluation failed for business: {}. Error: {}. Falling back to heuristic evaluation.",
                        business.getName(), e.getMessage());
            }
        } else {
            log.debug("Gemini AI disabled; using heuristic evaluation");
        }

        if (res == null) {
            res = generateHeuristicScores(profile);
        }

        applyThresholdsAndComputeOverall(profile, res);

        BusinessEvaluation eval = BusinessEvaluation.builder()
                .business(business)
                .brandingScore(res.brandingScore)
                .nameProfessionalismScore(res.nameScore)
                .emailProfessionalismScore(res.emailScore)
                .descriptionProfessionalismScore(res.descriptionScore)
                .locationScore(res.locationScore)
                .categoryScore(res.categoryScore)
                .overallScore(res.overall)
                .nameDetails(res.nameDetails)
                .emailDetails(res.emailDetails)
                .descriptionDetails(res.descriptionDetails)
                .brandingDetails(appendAiOverall(res.brandingDetails, res))
                .locationDetails(res.locationDetails)
                .categoryDetails(res.categoryDetails)
                .nameSuggestions(res.nameSuggestions)
                .emailSuggestions(res.emailSuggestions)
                .descriptionSuggestions(res.descriptionSuggestions)
                .brandingSuggestions(res.brandingSuggestions)
                .source(res.source)
                .build();

        return evaluationRepository.save(eval);
    }

    @Transactional
    public BusinessEvaluation updateEvaluation(Business business, boolean nameChanged, boolean emailChanged,
                                              boolean phoneChanged, boolean locationChanged,
                                              boolean descriptionChanged, boolean categoryChanged) {
        var evaluations = evaluationRepository.findByBusinessOrderByCreatedAtDesc(business);
        if (evaluations.isEmpty()) {
            return evaluateAndSave(business);
        }

        BusinessEvaluation existing = evaluations.get(0);
        boolean anyChanged = nameChanged || emailChanged || phoneChanged || locationChanged || descriptionChanged || categoryChanged;
        if (!anyChanged) {
            return existing; // nothing to do
        }

        BusinessProfile profile = new BusinessProfileAdapter(business);
        EvaluationResult res = null;
        if (aiEnabled) {
            try {
                res = aiEvaluateAll(profile);
            } catch (Exception e) {
                log.warn("Gemini AI evaluation failed during update. Falling back to heuristics. Error: {}", e.getMessage());
            }
        }
        if (res == null) {
            res = generateHeuristicScores(profile);
        }
        applyThresholdsAndComputeOverall(profile, res);

        existing.setBrandingScore(res.brandingScore);
        existing.setNameProfessionalismScore(res.nameScore);
        existing.setEmailProfessionalismScore(res.emailScore);
        existing.setDescriptionProfessionalismScore(res.descriptionScore);
        existing.setLocationScore(res.locationScore);
        existing.setCategoryScore(res.categoryScore);
        existing.setOverallScore(res.overall);
        existing.setNameDetails(res.nameDetails);
        existing.setEmailDetails(res.emailDetails);
        existing.setDescriptionDetails(res.descriptionDetails);
        existing.setBrandingDetails(appendAiOverall(res.brandingDetails, res));
        existing.setLocationDetails(res.locationDetails);
        existing.setCategoryDetails(res.categoryDetails);
        existing.setNameSuggestions(res.nameSuggestions);
        existing.setEmailSuggestions(res.emailSuggestions);
        existing.setDescriptionSuggestions(res.descriptionSuggestions);
        existing.setBrandingSuggestions(res.brandingSuggestions);
        existing.setSource(res.source);

        return evaluationRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> aiHealth() {
        java.util.Map<String, Object> out = new java.util.HashMap<>();
        out.put("enabled", aiEnabled);
        out.put("model", geminiModel);
        if (!aiEnabled) {
            out.put("ok", false);
            out.put("reason", "Gemini API key is not configured");
            return out;
        }
        try {
            String prompt = "Return ONLY this exact JSON: {\\n  \\\"ping\\\": \\\"ok\\\"\\n}";
            String body = objectMapper.writeValueAsString(java.util.Map.of(
                    "contents", new Object[]{
                            java.util.Map.of("parts", new Object[]{
                                    java.util.Map.of("text", prompt)
                            })
                    },
                    "generationConfig", java.util.Map.of(
                            "temperature", 0.0,
                            "maxOutputTokens", 50
                    )
            ));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
            out.put("status", resp.statusCode());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                String content = objectMapper.readTree(resp.body())
                        .path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
                out.put("contentPreview", content.length() > 120 ? content.substring(0, 120) + "..." : content);
                out.put("ok", !content.isBlank());
            } else {
                out.put("ok", false);
                out.put("errorBodyPreview", resp.body() == null ? null : (resp.body().length() > 200 ? resp.body().substring(0, 200) + "..." : resp.body()));
            }
        } catch (Exception e) {
            out.put("ok", false);
            out.put("exception", e.getMessage());
        }
        return out;
    }

    // --- AI one-shot evaluation across all dimensions ---
    private EvaluationResult aiEvaluateAll(BusinessProfile p) throws Exception {
        String prompt = """
            You are an expert business evaluator. Score and improve the following business data.
            Return ONLY valid JSON matching this schema:
            {
              "name":        { "score": 0-100, "details": string, "suggestion": string|null },
              "location":    { "score": 0-100, "details": string, "suggestion": string|null },
              "email":       { "score": 0-100, "details": string, "enhancement": string|null },
              "description": { "score": 0-100, "details": string, "suggestion": string|null },
              "category":    { "score": 0-100, "details": string, "suggestion": string|null },
              "branding":    { "score": 0-100, "details": string, "suggestion": string|null },
              "ai_overall": { "score": 0-100, "summary": string }
            }

            Scoring objective:
            - name: clarity, professionalism, uniqueness; suggestions only if score < 70.
            - location: clarity and standardized format (e.g., number, street, postal code, city, country); suggestion only if score < 80.
            - email: format validity and brand alignment; enhancement only if score < 85.
            - description: grammar, clarity, tone, content; suggestion only if score < 65 and provide a rewritten version.
            - category: alignment of name/description with selected category; if mismatch, low score and suggest a better category.
            - branding: consistency across name, email, description tone; if weak (score < 65), suggest fixes for brand consistency.

            Business:
            - Name: %s
            - Location: %s
            - Email: %s
            - Category: %s
            - Description: %s
            """.formatted(
                nz(p.getName()), nz(p.getLocation()), nz(p.getEmail()), nz(p.getCategoryName()), nz(p.getDescription())
        );

        String body = objectMapper.writeValueAsString(java.util.Map.of(
                "contents", new Object[]{
                        java.util.Map.of("parts", new Object[]{
                                java.util.Map.of("text", prompt)
                        })
                },
                "generationConfig", java.util.Map.of(
                        "temperature", temperature,
                        "maxOutputTokens", maxTokens
                )
        ));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new Exception("Gemini API status " + resp.statusCode() + ": " + resp.body());
        }
        if (resp.statusCode() == 200){
            log.info("Gemini API returned status 200 for business evaluation.");
        }

        String content = objectMapper.readTree(resp.body())
                .path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
        if (content.isBlank()) throw new Exception("Empty Gemini content");

        String cleaned = content.trim();
        if (cleaned.startsWith("```json")) cleaned = cleaned.substring(7);
        if (cleaned.startsWith("```") ) {
            int s = cleaned.indexOf('{');
            int e = cleaned.lastIndexOf('}');
            if (s >= 0 && e > s) cleaned = cleaned.substring(s, e + 1);
        }
        if (cleaned.endsWith("```") ) cleaned = cleaned.substring(0, cleaned.length() - 3);

        JsonNode json = objectMapper.readTree(cleaned.trim());
        EvaluationResult r = new EvaluationResult();
        r.source = "AI";

        // Modular parsing per dimension
        evaluateName(json.path("name"), r);
        evaluateLocation(json.path("location"), r);
        evaluateEmail(json.path("email"), r);
        evaluateDescription(json.path("description"), r);
        evaluateCategory(json.path("category"), r);
        evaluateBranding(json.path("branding"), r);

        // Optional AI overall summary
        r.aiOverallScore = clamp(json.path("ai_overall").path("score").asInt(r.overall));
        r.aiOverallSummary = json.path("ai_overall").path("summary").asText("");
        return r;
    }

    // Helper: Name evaluation mapping
    private void evaluateName(JsonNode n, EvaluationResult r) {
        r.nameScore = clamp(n.path("score").asInt(50));
        r.nameDetails = safeText(n, "details");
        // Accept both "suggestion" and "suggestions"
        String s = n.path("suggestion").asText("");
        if (s.isBlank()) s = n.path("suggestions").asText("");
        r.nameSuggestions = nullIfBlank(s);
    }

    // Helper: Email evaluation mapping
    private void evaluateEmail(JsonNode n, EvaluationResult r) {
        r.emailScore = clamp(n.path("score").asInt(50));
        r.emailDetails = safeText(n, "details");
        // Model might return either "enhancement" or "suggestion(s)"
        String s = n.path("enhancement").asText("");
        if (s.isBlank()) s = n.path("suggestion").asText("");
        if (s.isBlank()) s = n.path("suggestions").asText("");
        r.emailSuggestions = nullIfBlank(s);
    }

    // Helper: Description evaluation mapping
    private void evaluateDescription(JsonNode n, EvaluationResult r) {
        r.descriptionScore = clamp(n.path("score").asInt(50));
        r.descriptionDetails = safeText(n, "details");
        String s = n.path("suggestion").asText("");
        if (s.isBlank()) s = n.path("suggestions").asText("");
        r.descriptionSuggestions = nullIfBlank(s);
    }

    // Helper: Location evaluation mapping
    private void evaluateLocation(JsonNode n, EvaluationResult r) {
        r.locationScore = clamp(n.path("score").asInt(50));
        r.locationDetails = safeText(n, "details");
        // We'll inline suggestion into details later based on thresholds
        String s = n.path("suggestion").asText("");
        if (s.isBlank()) s = n.path("suggestions").asText("");
        r.locationSuggestions = nullIfBlank(s);
    }

    // Helper: Category evaluation mapping
    private void evaluateCategory(JsonNode n, EvaluationResult r) {
        r.categoryScore = clamp(n.path("score").asInt(50));
        r.categoryDetails = safeText(n, "details");
        String s = n.path("suggestion").asText("");
        if (s.isBlank()) s = n.path("suggestions").asText("");
        r.categorySuggestions = nullIfBlank(s);
    }

    // Helper: Branding evaluation mapping
    private void evaluateBranding(JsonNode n, EvaluationResult r) {
        r.brandingScore = clamp(n.path("score").asInt(50));
        r.brandingDetails = safeText(n, "details");
        String s = n.path("suggestion").asText("");
        if (s.isBlank()) s = n.path("suggestions").asText("");
        r.brandingSuggestions = nullIfBlank(s);
    }

    private void applyThresholdsAndComputeOverall(BusinessProfile p, EvaluationResult r) {
        // Threshold-based suggestion visibility and inlining where columns don't exist
        if (r.nameScore >= 70) r.nameSuggestions = null;

        if (r.locationScore < 80 && !isBlank(r.locationSuggestions)) {
            r.locationDetails = (isBlank(r.locationDetails) ? "" : r.locationDetails + "\n") + "Suggestion: " + r.locationSuggestions;
        }
        r.locationSuggestions = null; // no column for location suggestion

        if (r.emailScore >= 85) r.emailSuggestions = null;
        if (r.descriptionScore >= 65) r.descriptionSuggestions = null;

        if (r.brandingScore >= 65) r.brandingSuggestions = null;

        if (r.categoryScore < 60 && !isBlank(r.categorySuggestions)) {
            r.categoryDetails = (isBlank(r.categoryDetails) ? "" : r.categoryDetails + "\n") + "Suggestion: " + r.categorySuggestions;
        }
        r.categorySuggestions = null; // no column for category suggestion

        // Minimal default details if AI left blank
        if (isBlank(r.nameDetails)) r.nameDetails = "Evaluated clarity, professionalism, and uniqueness.";
        if (isBlank(r.locationDetails)) r.locationDetails = "Evaluated address clarity and standardized format.";
        if (isBlank(r.emailDetails)) r.emailDetails = "Checked format validity and brand alignment.";
        if (isBlank(r.descriptionDetails)) r.descriptionDetails = "Assessed grammar, clarity, tone, and content.";
        if (isBlank(r.categoryDetails)) r.categoryDetails = "Checked alignment of name/description with selected category.";
        if (isBlank(r.brandingDetails)) r.brandingDetails = "Checked name, email, and description consistency.";

        // Compute weighted overall (equal weights for 6 fields)
        double overall = r.nameScore * W_NAME + r.emailScore * W_EMAIL + r.descriptionScore * W_DESC +
                         r.locationScore * W_LOC + r.categoryScore * W_CAT + r.brandingScore * W_BRAND;
        r.overall = clamp((int)Math.round(overall));

        // No phone enrichment: entity has no phone columns
    }

    private String appendAiOverall(String brandingDetails, EvaluationResult r) {
        if (isBlank(r.aiOverallSummary)) return brandingDetails;
        String base = isBlank(brandingDetails) ? "" : brandingDetails.trim() + "\n";
        return base + "AI Overall: " + r.aiOverallScore + "/100 — " + r.aiOverallSummary;
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String nullIfBlank(String s) { return (s == null || s.isBlank()) ? null : s; }

    // --- existing NOT USED methods ---
    private EvaluationResult evaluateField(BusinessProfile profile, String fieldName) {
        if (!aiEnabled) {
            log.debug("Gemini AI disabled; computing heuristic result for field: {}", fieldName);
            return evaluateFieldCalculated(profile, fieldName);
        }

        try {
            log.debug("Attempting Gemini AI evaluation for field: {}", fieldName);

            String prompt;
            if (fieldName.equals("category")) {
                // Special validation for category match - no score, just validation
                prompt = String.format("""
                    You are a business categorization expert.
                    
                    Analyze if this business information matches the selected category '%s'.
                    
                    Business Information:
                    - Name: %s
                    - Description: %s
                    - Category selected: %s
                    
                    Check if the name and description are appropriate for this category.
                    If they DON'T match, give a WARNING and explain why.
                    If they DO match, confirm it's appropriate.
                    
                    Return ONLY this JSON format (no score for category, just validation):
                    {
                      "category": {
                        "score": 0,
                        "details": "<validation result: does this business match the category or not?>",
                        "suggestions": "<WARNING if mismatch, or confirmation if appropriate>"
                      }
                    }
                    """, nz(profile.getCategoryName()), nz(profile.getName()),
                    nz(profile.getDescription()), nz(profile.getCategoryName()));
            } else {
                prompt = String.format("""
                    You are a business evaluation expert providing professional consulting advice.
                    
                    Analyze ONLY the %s of this business and provide:
                    1. A professionalism score (0-100)
                    2. Detailed analysis of strengths and weaknesses
                    3. Specific, actionable recommendations for improvement
                    
                    Business Information:
                    - Name: %s
                    - Location: %s
                    - Phone: %s
                    - Email: %s
                    - Category: %s
                    - Description: %s
                    
                    IMPORTANT: Consider if the %s is appropriate for a business in the '%s' category.
                    If it doesn't match the category, reduce the score significantly and warn the user.
                    
                    Return ONLY this JSON format (no markdown, no extra text):
                    {
                      "%s": {
                        "score": <number 0-100>,
                        "details": "<professional analysis>",
                        "suggestions": "<specific actionable advice>"
                      }
                    }
                    """, fieldName,
                    nz(profile.getName()), nz(profile.getLocation()), nz(profile.getPhone()),
                    nz(profile.getEmail()), nz(profile.getCategoryName()), nz(profile.getDescription()),
                    fieldName, nz(profile.getCategoryName()), fieldName);
            }

            // Google Gemini API request format
            String body = objectMapper.writeValueAsString(java.util.Map.of(
                    "contents", new Object[]{
                            java.util.Map.of("parts", new Object[]{
                                    java.util.Map.of("text", prompt)
                            })
                    },
                    "generationConfig", java.util.Map.of(
                            "temperature", temperature,
                            "maxOutputTokens", 800
                    )
            ));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                log.warn("Gemini API returned status {} for field: {}. Response: {}", resp.statusCode(), fieldName, resp.body());
                return evaluateFieldCalculated(profile, fieldName);
            }

            // Parse Gemini response
            JsonNode responseJson = objectMapper.readTree(resp.body());
            String content = responseJson.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
            if (content.isBlank()) {
                log.warn("Empty response from Gemini for field: {}", fieldName);
                return evaluateFieldCalculated(profile, fieldName);
            }

            // Clean JSON markers
            String cleaned = content.trim();
            if (cleaned.startsWith("```json")) {
                cleaned = cleaned.substring(7);
            }
            if (cleaned.startsWith("```") ) {
                int idx = cleaned.indexOf('{');
                int last = cleaned.lastIndexOf('}');
                if (idx >= 0 && last > idx) cleaned = cleaned.substring(idx, last + 1);
            }
            if (cleaned.endsWith("```") ) {
                cleaned = cleaned.substring(0, cleaned.length() - 3);
            }

            JsonNode json = objectMapper.readTree(cleaned.trim());
            EvaluationResult r = new EvaluationResult();
            r.source = "GEMINI-AI";

            JsonNode fieldNode = json.path(fieldName);
            switch (fieldName) {
                case "name" -> {
                    r.nameScore = clamp(fieldNode.path("score").asInt(50));
                    r.nameDetails = safeText(fieldNode, "details");
                    r.nameSuggestions = safeText(fieldNode, "suggestions");
                }
                case "email" -> {
                    r.emailScore = clamp(fieldNode.path("score").asInt(50));
                    r.emailDetails = safeText(fieldNode, "details");
                    r.emailSuggestions = safeText(fieldNode, "suggestions");
                }
                case "description" -> {
                    r.descriptionScore = clamp(fieldNode.path("score").asInt(50));
                    r.descriptionDetails = safeText(fieldNode, "details");
                    r.descriptionSuggestions = safeText(fieldNode, "suggestions");
                }
                case "location" -> {
                    r.locationScore = clamp(fieldNode.path("score").asInt(50));
                    r.locationDetails = safeText(fieldNode, "details");
                    r.locationSuggestions = safeText(fieldNode, "suggestions");
                }
                case "phone" -> {
                    r.phoneScore = clamp(fieldNode.path("score").asInt(50));
                    r.phoneDetails = safeText(fieldNode, "details");
                    r.phoneSuggestions = safeText(fieldNode, "suggestions");
                }
                case "category" -> {
                    r.categoryScore = clamp(fieldNode.path("score").asInt(50));
                    r.categoryDetails = safeText(fieldNode, "details");
                    r.categorySuggestions = safeText(fieldNode, "suggestions");
                }
                case "branding" -> {
                    r.brandingScore = clamp(fieldNode.path("score").asInt(50));
                    r.brandingDetails = safeText(fieldNode, "details");
                    r.brandingSuggestions = safeText(fieldNode, "suggestions");
                }
            }

            log.info("Gemini AI evaluation successful for field: {}", fieldName);
            return r;
        } catch (Exception e) {
            log.warn("Gemini AI evaluation failed for field: {}. Error: {}. Using calculated scores.", fieldName, e.getMessage());
            return evaluateFieldCalculated(profile, fieldName);
        }
    }

    private EvaluationResult evaluateFieldCalculated(BusinessProfile profile, String fieldName) {
        EvaluationResult all = generateHeuristicScores(profile);
        EvaluationResult r = new EvaluationResult();
        r.source = all.source;
        switch (fieldName) {
            case "name" -> {
                r.nameScore = all.nameScore;
                r.nameDetails = all.nameDetails;
                r.nameSuggestions = all.nameSuggestions;
            }
            case "email" -> {
                r.emailScore = all.emailScore;
                r.emailDetails = all.emailDetails;
                r.emailSuggestions = all.emailSuggestions;
            }
            case "description" -> {
                r.descriptionScore = all.descriptionScore;
                r.descriptionDetails = all.descriptionDetails;
                r.descriptionSuggestions = all.descriptionSuggestions;
            }
            case "location" -> {
                r.locationScore = all.locationScore;
                r.locationDetails = all.locationDetails;
                r.locationSuggestions = all.locationSuggestions;
            }
            case "phone" -> {
                r.phoneScore = all.phoneScore;
                r.phoneDetails = all.phoneDetails;
                r.phoneSuggestions = all.phoneSuggestions;
            }
            case "category" -> {
                r.categoryScore = all.categoryScore;
                r.categoryDetails = all.categoryDetails;
                r.categorySuggestions = all.categorySuggestions;
            }
            case "branding" -> {
                r.brandingScore = all.brandingScore;
                r.brandingDetails = all.brandingDetails;
                r.brandingSuggestions = all.brandingSuggestions;
            }
        }
        return r;
    }

    private EvaluationResult generateHeuristicScores(BusinessProfile p) {
        EvaluationResult r = new EvaluationResult();
        r.source = "HEURISTIC";

        String name = nz(p.getName());
        String email = nz(p.getEmail());
        String location = nz(p.getLocation());
        String description = nz(p.getDescription());
        String category = nz(p.getCategoryName());
        String cityToken = extractCityToken(location);

        // Name score and suggestions
        int nameScore = 100;
        if (name.isBlank()) nameScore = 20; else {
            if (name.length() < 3) nameScore -= 40;
            if (name.length() > 40) nameScore -= 20;
            if (DIGITS_REGEX.matcher(name).matches()) nameScore -= 20;
            if (BAD_NAME_CHARS.matcher(name).find()) nameScore -= 10;
            if (!containsIgnoreCase(name, category)) nameScore -= 5; // encourage alignment with category
        }
        nameScore = clamp(nameScore);
        r.nameScore = nameScore;
        r.nameDetails = name.isBlank() ? "Missing business name." : "Checked length, characters, alignment with category.";
        r.nameSuggestions = nameScore < 70 ? suggestNames(name, category, cityToken) : "Looks good.";

        // Location score and suggestions
        int locationScore = 100;
        boolean hasNumber = DIGITS_REGEX.matcher(location).matches();
        boolean hasStreetWord = containsStreetWord(location);
        boolean hasPostal = POSTAL_REGEX.matcher(location).find();
        boolean hasComma = location.contains(",");
        if (location.isBlank()) locationScore = 20; else {
            if (!hasNumber) locationScore -= 20;
            if (!hasStreetWord) locationScore -= 20;
            if (!hasPostal) locationScore -= 15;
            if (!hasComma) locationScore -= 10;
            if (location.length() < 10) locationScore -= 15;
        }
        locationScore = clamp(locationScore);
        r.locationScore = locationScore;
        r.locationDetails = location.isBlank() ? "Missing address." : "Checked number, street type, postal code, city formatting.";
        r.locationSuggestions = locationScore < 80 ? "Use format: '123 Rue de Paris, 75001 Paris, FR'. Include street number, type, postal code, city, country." : "Looks good.";

        // Email score and suggestions
        int emailScore = 100;
        String emailDomain = extractDomain(email);
        boolean emailValid = EMAIL_REGEX.matcher(email).matches();
        if (email.isBlank()) emailScore = 20; else {
            if (!emailValid) emailScore -= 40;
            if (GENERIC_EMAIL_DOMAINS.contains(emailDomain)) emailScore -= 20;
            String expectedDomain = expectedDomainFromName(name);
            if (!emailDomain.equalsIgnoreCase(expectedDomain) && !GENERIC_EMAIL_DOMAINS.contains(emailDomain)) emailScore -= 10;
        }
        emailScore = clamp(emailScore);
        r.emailScore = emailScore;
        r.emailDetails = email.isBlank() ? "Missing email." : (emailValid ? "Valid format." : "Invalid email format.");
        r.emailSuggestions = emailScore < 85 ? suggestEmailImprovements(expectedDomainFromName(name)) : "good";

        // Description score and suggestions
        int descriptionScore = 0;
        int len = description.length();
        if (len == 0) descriptionScore = 10;
        else if (len < 40) descriptionScore = 50;
        else if (len < 100) descriptionScore = 65;
        else if (len < 300) descriptionScore = 80;
        else descriptionScore = 90;
        if (!containsIgnoreCase(description, category)) descriptionScore -= 5;
        if (!cityToken.isBlank() && !containsIgnoreCase(description, cityToken)) descriptionScore -= 5;
        descriptionScore = clamp(descriptionScore);
        r.descriptionScore = descriptionScore;
        r.descriptionDetails = len == 0 ? "Missing description." : "Checked length, clarity, category/city relevance.";
        r.descriptionSuggestions = descriptionScore < 65 ? suggestDescription(category, cityToken) : "Looks good.";

        // Category score and suggestions
        int categoryScore = 0;
        if (category.isBlank()) {
            categoryScore = 0;
            r.categoryDetails = "No category selected.";
            r.categorySuggestions = "Select the most relevant category for your activity.";
        } else {
            boolean nameHas = containsIgnoreCase(name, category);
            boolean descHas = containsIgnoreCase(description, category);
            if (nameHas && descHas) categoryScore = 90; else if (nameHas || descHas) categoryScore = 75; else categoryScore = 25;
            r.categoryDetails = nameHas || descHas ? "Category appears consistent with name/description." : "Category does not match provided name/description.";
            if (categoryScore < 60) r.categorySuggestions = "Consider changing category to match your activity (e.g., " + guessCategory(name + " " + description) + ")."; else r.categorySuggestions = "Looks good.";
        }
        categoryScore = clamp(categoryScore);
        r.categoryScore = categoryScore;

        // Branding score and suggestions
        int brandingScore = 100;
        if (nameScore < 70) brandingScore -= 15;
        if (emailScore < 85) brandingScore -= 15;
        if (descriptionScore < 65) brandingScore -= 20;
        if (locationScore < 80) brandingScore -= 10;
        if (categoryScore < 60) brandingScore -= 20;
        brandingScore = clamp(brandingScore);
        r.brandingScore = brandingScore;
        r.brandingDetails = "Assessed consistency between name, email, description, location, category.";
        r.brandingSuggestions = brandingScore < 65 ? "Secure a domain matching your brand, use a domain email (e.g., info@" + expectedDomainFromName(name) + "), create a simple logo and tagline, keep the same name across platforms." : "Looks good.";

        // Phone score (kept for completeness)
        int phoneScore = 0;
        String phone = nz(p.getPhone());
        int digits = phone.replaceAll("\\D", "").length();
        if (digits == 0) phoneScore = 0; else if (digits < 9) phoneScore = 40; else if (digits <= 15) phoneScore = 85; else phoneScore = 60;
        r.phoneScore = phoneScore;
        r.phoneDetails = digits == 0 ? "Missing phone." : "Digits count checked.";
        r.phoneSuggestions = digits < 9 ? "Provide a complete phone number, preferably in international format (e.g., +33 1 23 45 67 89)." : "Looks good.";

        // Overall (requested fields: name, location, email, description, category, branding)
        r.overall = (nameScore + locationScore + emailScore + descriptionScore + categoryScore + brandingScore) / 6;

        return r;
    }

    // Heuristic utility methods
    private static boolean containsIgnoreCase(String hay, String needle) {
        return !needle.isBlank() && hay.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
    }

    private static String extractDomain(String email) {
        int at = email.indexOf('@');
        return at > 0 ? email.substring(at + 1).toLowerCase(Locale.ROOT) : "";
    }

    private static String expectedDomainFromName(String name) {
        String base = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (base.isBlank()) base = "yourbusiness";
        return base + ".com";
    }

    private static boolean containsStreetWord(String location) {
        String s = location.toLowerCase(Locale.ROOT);
        return s.contains("street") || s.contains("st ") || s.contains(" avenue") || s.contains(" ave") || s.contains(" road") || s.contains(" rd")
                || s.contains("boulevard") || s.contains("blvd") || s.contains("rue") || s.contains("chemin") || s.contains("impasse") || s.contains("place");
    }

    private static String suggestNames(String current, String category, String city) {
        String base = current.isBlank() ? (category.isBlank() ? "Your Brand" : capitalize(category)) : current.trim();
        String cityPart = city.isBlank() ? "" : (" " + city);
        return "Try clearer, category-aligned names like: '" + base + cityPart + "', '" + capitalize(category) + cityPart + " Studio', '" + base + " " + capitalize(category) + "'.";
    }

    private static String suggestEmailImprovements(String domain) {
        return "Use a domain-based email for professionalism, e.g., 'info@" + domain + "', 'contact@" + domain + "', 'hello@" + domain + "'.";
    }

    private static String suggestDescription(String category, String city) {
        String cat = capitalize(category.isBlank() ? "your service" : category);
        String loc = city.isBlank() ? "your city" : city;
        return "Write 3-5 sentences covering: what you do (" + cat + "), who you serve in " + loc + ", your strengths, services list, and a simple call-to-action.";
    }

    private static String guessCategory(String text) {
        String s = text.toLowerCase(Locale.ROOT);
        if (s.contains("barber") || s.contains("hair") || s.contains("salon")) return "Barber / Salon";
        if (s.contains("restaurant") || s.contains("cafe") || s.contains("coffee")) return "Restaurant / Cafe";
        if (s.contains("gym") || s.contains("fitness") || s.contains("yoga")) return "Fitness / Gym";
        if (s.contains("clinic") || s.contains("doctor") || s.contains("dentist")) return "Clinic / Healthcare";
        if (s.contains("law") || s.contains("attorney")) return "Legal Services";
        if (s.contains("real estate") || s.contains("realtor")) return "Real Estate";
        return "General Services";
    }

    private static String capitalize(String s) {
        if (s == null || s.isBlank()) return s;
        return s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1);
    }

    private static int clamp(int v) { return Math.max(0, Math.min(100, v)); }

    private static String nz(String s) { return s == null ? "" : s; }

    private static String safeText(JsonNode node, String field) {
        String value = node.path(field).asText("");
        // Return empty if missing; we'll fill defaults later in applyThresholdsAndComputeOverall
        return (value == null) ? "" : value.trim();
    }

    private static class EvaluationResult {
        int brandingScore;
        int nameScore;
        int emailScore;
        int descriptionScore;
        int locationScore;
        int phoneScore;
        int categoryScore;
        int overall;
        String brandingDetails;
        String brandingSuggestions;
        String nameDetails;
        String nameSuggestions;
        String emailDetails;
        String emailSuggestions;
        String descriptionDetails;
        String descriptionSuggestions;
        String locationDetails;
        String locationSuggestions;
        String phoneDetails;
        String phoneSuggestions;
        String categoryDetails;
        String categorySuggestions;
        String source;

        // transient AI overall score
        int aiOverallScore;
        // transient AI overall summary
        String aiOverallSummary;
    }

    private static String extractCityToken(String location) {
        if (location == null || location.isBlank()) return "";
        try {
            // Prefer the last meaningful comma-separated segment as city
            String[] parts = location.split(",");
            for (int i = parts.length - 1; i >= 0; i--) {
                String seg = parts[i].trim();
                if (seg.isEmpty()) continue;
                // Remove postal codes/digits
                String noDigits = seg.replaceAll("\\d", "").trim();
                if (noDigits.isEmpty()) continue;
                // Skip likely country codes (2-letter)
                if (noDigits.matches("^[A-Za-z]{2}$")) continue;
                // Choose first word with letters
                for (String w : noDigits.split("\\s+")) {
                    if (w.length() >= 3 && w.matches("^[A-Za-zÀ-ÖØ-öø-ÿ'.-]+$")) {
                        return w;
                    }
                }
                return noDigits;
            }
            return "";
        } catch (Exception ignored) {
            return "";
        }
    }
}
