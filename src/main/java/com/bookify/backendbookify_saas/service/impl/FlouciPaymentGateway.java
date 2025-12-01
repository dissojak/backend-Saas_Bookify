package com.bookify.backendbookify_saas.service.impl;

import com.bookify.backendbookify_saas.config.FlouciConfig;
import com.bookify.backendbookify_saas.service.PaymentGateway;
import com.bookify.backendbookify_saas.service.PaymentGatewayResponse;
import com.bookify.backendbookify_saas.service.PaymentVerificationResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Implémentation Flouci de la passerelle de paiement
 */
public class FlouciPaymentGateway implements PaymentGateway {

    private final Logger log = LoggerFactory.getLogger(FlouciPaymentGateway.class);
    private final RestTemplate restTemplate;
    private final FlouciConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public FlouciPaymentGateway(RestTemplate flouciRestTemplate, FlouciConfig config) {
        this.restTemplate = flouciRestTemplate;
        this.config = config;
    }

    @Override
    public PaymentGatewayResponse generatePayment(long amount, String successLink, String failLink, String developerTrackingId) {
        try {
            // Construire le payload EXACTEMENT comme dans le code Express.js qui fonctionne
            Map<String, Object> payload = new HashMap<>();
            payload.put("app_token", "d01440af-5a3b-4c9f-8567-6c0f964d1ef7");
            payload.put("app_secret", "dd3163a3-a4ad-4ec5-8875-e5658b3ef0ff");
            payload.put("amount", amount);
            payload.put("accept_card", "true");
            payload.put("session_timeout_secs", 1200);
            payload.put("success_link", successLink);
            payload.put("fail_link", failLink);
            payload.put("developer_tracking_id", "a702c74a-9a4d-4f36-b18d-b76f63b7bef8");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String url = "https://developers.flouci.com/api/generate_payment";
            log.info("Calling Flouci generate_payment: {}", url);

            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);

            if (resp.getBody() == null) {
                return new PaymentGatewayResponse(null, null, false, "Empty response from Flouci");
            }

            log.info("Flouci response: {}", resp.getBody());

            // Parser la réponse selon la vraie structure Flouci
            JsonNode root = mapper.readTree(resp.getBody());
            JsonNode result = root.path("result");

            boolean success = result.path("success").asBoolean(false);
            if (!success) {
                String errorMessage = result.has("error") ? result.path("error").asText() : "Unknown error";
                log.error("Flouci payment failed: error={}, full response={}", errorMessage, resp.getBody());
                return new PaymentGatewayResponse(null, null, false, "Flouci error: " + errorMessage);
            }

            // Récupérer le lien de paiement et l'ID
            String link = result.path("link").asText(null);
            String paymentId = result.path("payment_id").asText(null);

            log.info("Flouci payment generated: paymentId={}, link={}", paymentId, link);

            return new PaymentGatewayResponse(paymentId, link, true, null);

        } catch (Exception e) {
            log.error("Error generating Flouci payment", e);
            return new PaymentGatewayResponse(null, null, false, e.getMessage());
        }
    }

    @Override
    public PaymentVerificationResult verifyPayment(String paymentId) {
        try {
            String url = "https://developers.flouci.com/api/verify_payment/" + paymentId;

            String appPublic = config.getAppPublic() != null && !config.getAppPublic().isEmpty()
                    ? config.getAppPublic()
                    : "d01440af-5a3b-4c9f-8567-6c0f964d1ef7";
            String appSecret = config.getAppSecret() != null && !config.getAppSecret().isEmpty()
                    ? config.getAppSecret()
                    : "dd3163a3-a4ad-4ec5-8875-e5658b3ef0ff";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apppublic", appPublic);
            headers.set("appsecret", appSecret);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (resp.getBody() == null) {
                return new PaymentVerificationResult(paymentId, "UNKNOWN", null, false);
            }

            JsonNode root = mapper.readTree(resp.getBody());
            JsonNode result = root.path("result");

            String status = result.path("status").asText("UNKNOWN");
            Long amountResult = result.has("amount") ? result.path("amount").asLong() : null;

            boolean verified = "SUCCESS".equalsIgnoreCase(status);

            log.info("Flouci payment verified: paymentId={}, status={}, amount={}", paymentId, status, amountResult);

            return new PaymentVerificationResult(paymentId, status, amountResult, verified);

        } catch (Exception e) {
            log.error("Error verifying Flouci payment", e);
            return new PaymentVerificationResult(paymentId, "ERROR", null, false);
        }
    }

    @Override
    public String getGatewayName() {
        return "FLOUCI";
    }
}

