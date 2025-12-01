package com.bookify.backendbookify_saas.service.impl;

import com.bookify.backendbookify_saas.config.FlouciConfig;
import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentRequest;
import com.bookify.backendbookify_saas.models.dtos.FlouciGeneratePaymentResponse;
import com.bookify.backendbookify_saas.models.dtos.FlouciVerifyPaymentResponse;
import com.bookify.backendbookify_saas.service.FlouciPaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlouciPaymentServiceImpl implements FlouciPaymentService {

    private final Logger log = LoggerFactory.getLogger(FlouciPaymentServiceImpl.class);
    private final RestTemplate restTemplate;
    private final FlouciConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public FlouciPaymentServiceImpl(RestTemplate flouciRestTemplate, FlouciConfig config) {
        this.restTemplate = flouciRestTemplate;
        this.config = config;
    }

    @Override
    public FlouciGeneratePaymentResponse generatePayment(FlouciGeneratePaymentRequest request) {
        try {
            // Determine success/fail links and developer tracking id: prefer request values, else use built-in defaults
            String successLink = request.getSuccessLink();
            String failLink = request.getFailLink();
            String developerTrackingId = request.getDeveloperTrackingId();

            if (successLink == null || successLink.isEmpty()) {
                // default built-in success link (can be changed later to come from config)
                successLink = "http://localhost:3000/subscription/success";
            }
            if (failLink == null || failLink.isEmpty()) {
                failLink = "http://localhost:3000/subscription/fail";
            }
            if (developerTrackingId == null || developerTrackingId.isEmpty()) {
                // fallback to value from FlouciConfig (populated from .env via DotenvConfig)
                developerTrackingId = config.getDeveloperTrackingId();
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("app_token", config.getAppToken());
            payload.put("app_secret", config.getAppSecret());
            payload.put("amount", request.getAmount());
            payload.put("accept_card", request.getAcceptCard());
            payload.put("session_timeout_secs", request.getSessionTimeoutSecs());

            // use constructed links and tracking id
            payload.put("success_link", successLink);
            payload.put("fail_link", failLink);
            if (developerTrackingId != null && !developerTrackingId.isEmpty()) {
                payload.put("developer_tracking_id", developerTrackingId);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            String url = config.getBaseUrl();
            ResponseEntity<String> resp = restTemplate.postForEntity(url, entity, String.class);
            if (resp.getBody() == null) {
                return new FlouciGeneratePaymentResponse(null, null, "ERROR", null);
            }

            JsonNode node = mapper.readTree(resp.getBody());
            JsonNode result = node.path("result");
            String paymentId = result.path("payment_id").asText(null);
            if (paymentId == null || paymentId.isEmpty()) {
                paymentId = result.path("id").asText(null);
            }
            String checkout = result.path("checkout_url").asText(null);
            String status = result.path("status").asText("PENDING");
            String raw = mapper.writeValueAsString(node);

            JsonNode paymentInfo = node.path("data");
            log.info("Flouci payment generated: paymentInfo={}" , paymentInfo);

            return new FlouciGeneratePaymentResponse(paymentId, checkout, status, raw);
        } catch (Exception e) {
            log.error("generatePayment error", e);
            return new FlouciGeneratePaymentResponse(null, null, "ERROR", e.getMessage());
        }
    }

    @Override
    public FlouciVerifyPaymentResponse verifyPayment(String paymentId) {
        try {
            String url = config.getBaseUrl() + "/api/verify_payment/" + paymentId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (config.getAppPublic() != null && !config.getAppPublic().isEmpty()) {
                headers.set("apppublic", config.getAppPublic());
            }
            if (config.getAppSecret() != null && !config.getAppSecret().isEmpty()) {
                headers.set("appsecret", config.getAppSecret());
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            if (resp.getBody() == null) {
                return new FlouciVerifyPaymentResponse(paymentId, "UNKNOWN", null, null);
            }
            JsonNode node = mapper.readTree(resp.getBody());
            JsonNode result = node.path("result");
            String status = result.path("status").asText("UNKNOWN");
            Long amount = null;
            if (result.has("amount")) amount = result.path("amount").asLong();
            String raw = mapper.writeValueAsString(node);
            return new FlouciVerifyPaymentResponse(paymentId, status, amount, raw);
        } catch (Exception e) {
            log.error("verifyPayment error", e);
            return new FlouciVerifyPaymentResponse(paymentId, "ERROR", null, e.getMessage());
        }
    }
}
