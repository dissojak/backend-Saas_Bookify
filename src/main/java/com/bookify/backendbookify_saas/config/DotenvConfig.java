package com.bookify.backendbookify_saas.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

import java.util.Objects;

/**
 * Configuration class for loading environment variables using the Dotenv library.
 * This class is responsible for initializing system properties required for the application.
 */
@Configuration
public class DotenvConfig {

    /**
     * Initializes the application by loading environment variables from a `.env` file
     * and setting them as system properties. This method is executed after the bean
     * is constructed due to the `@PostConstruct` annotation.
     *
     * <p>Environment variables loaded:
     * <ul>
     *   <li><b>SPRING_MAIL_PASSWORD</b>: Password for Spring Mail configuration.</li>
     *   <li><b>GEMINI_API_KEY</b>: API key for Gemini integration.</li>
     *   <li><b>FLOUCI_APP_TOKEN</b>: App token for Flouci integration.</li>
     *   <li><b>FLOUCI_APP_SECRET</b>: App secret for Flouci integration.</li>
     *   <li><b>FLOUCI_APP_PUBLIC</b>: App public key for Flouci integration.</li>
     *   <li><b>FLOUCI_BASE_URL</b>: Base URL for Flouci API.</li>
     *   <li><b>FLOUCI_WEBHOOK_SECRET</b>: Webhook secret for Flouci integration.</li>
     *   <li><b>DEVELOPER_TRACKING_ID</b>: Developer tracking ID for Flouci requests.</li>
     * </ul>
     *
     * @throws NullPointerException if any required environment variable is missing.
     */
    @PostConstruct
    public void init() {
        // Load environment variables from the .env file
        Dotenv dotenv = Dotenv.load();

        // Set system properties for Spring Mail and Gemini API
        System.setProperty("spring.mail.password", Objects.requireNonNull(dotenv.get("SPRING_MAIL_PASSWORD")));
        System.setProperty("GEMINI_API_KEY", Objects.requireNonNull(dotenv.get("GEMINI_API_KEY")));

        // --- Added: Load Flouci related env vars (non-breaking; only set if present) ---
        String flouciToken = dotenv.get("FLOUCI_APP_TOKEN");
        if (flouciToken != null && !flouciToken.isEmpty()) {
            System.setProperty("flouci.app-token", flouciToken);
        }

        String flouciSecret = dotenv.get("FLOUCI_APP_SECRET");
        if (flouciSecret != null && !flouciSecret.isEmpty()) {
            System.setProperty("flouci.app-secret", flouciSecret);
        }

        String flouciPublic = dotenv.get("FLOUCI_APP_PUBLIC");
        if (flouciPublic != null && !flouciPublic.isEmpty()) {
            System.setProperty("flouci.app-public", flouciPublic);
        }

        String flouciBase = dotenv.get("FLOUCI_BASE_URL");
        if (flouciBase != null && !flouciBase.isEmpty()) {
            System.setProperty("flouci.base-url", flouciBase);
        }

        String flouciWebhookSecret = dotenv.get("FLOUCI_WEBHOOK_SECRET");
        if (flouciWebhookSecret != null && !flouciWebhookSecret.isEmpty()) {
            System.setProperty("flouci.webhook-secret", flouciWebhookSecret);
        }

        // NEW: developer tracking id used by Flouci requests
        String devTrack = dotenv.get("FLOUCI_DEVELOPER_TRACKING_ID");
        if (devTrack != null && !devTrack.isEmpty()) {
            System.setProperty("flouci.developer-tracking-id", devTrack);
        }
        // --- end Flouci vars ---

        // Log a message to indicate successful loading of environment variables
        System.out.println("         =========================================================");
        System.out.println("        |                                                         |");
        System.out.println("        |         ✅ Dotenv loaded and system properties.         |");
        System.out.println("        |                                                         |");
        System.out.println("         =========================================================");

        // Log Flouci config for debugging
        System.out.println("🔑 Flouci Configuration:");
        System.out.println("   - App Token: " + (flouciToken != null ? flouciToken.substring(0, Math.min(10, flouciToken.length())) + "..." : "NOT SET"));
        System.out.println("   - App Secret: " + (flouciSecret != null ? flouciSecret.substring(0, Math.min(10, flouciSecret.length())) + "..." : "NOT SET"));
        System.out.println("   - App Public: " + (flouciPublic != null ? flouciPublic.substring(0, Math.min(10, flouciPublic.length())) + "..." : "NOT SET"));
        System.out.println("   - Base URL: " + (flouciBase != null ? flouciBase : "NOT SET"));
        System.out.println("   - Developer Tracking ID: " + (devTrack != null ? devTrack.substring(0, Math.min(10, devTrack.length())) + "..." : "NOT SET"));
    }
}
