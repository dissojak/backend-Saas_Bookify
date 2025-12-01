package com.bookify.backendbookify_saas.config;

import com.bookify.backendbookify_saas.service.PaymentGateway;
import com.bookify.backendbookify_saas.service.impl.FlouciPaymentGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FlouciConfig {

    @Value("${flouci.base-url:https://developers.flouci.com}")
    private String baseUrl;

    @Value("${flouci.app-token:}")
    private String appToken;

    @Value("${flouci.app-secret:}")
    private String appSecret;

    @Value("${flouci.app-public:}")
    private String appPublic;

    @Value("${flouci.developer-tracking-id:}")
    private String developerTrackingId;

    @Bean
    public RestTemplate flouciRestTemplate() {
        return new RestTemplate();
    }

    @Bean("flouciPaymentGateway")
    public PaymentGateway flouciPaymentGateway(RestTemplate flouciRestTemplate) {
        return new FlouciPaymentGateway(flouciRestTemplate, this);
    }

    public String getAppToken() {
        return appToken;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public String getAppPublic() {
        return appPublic;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDeveloperTrackingId() {
        return developerTrackingId;
    }
}
