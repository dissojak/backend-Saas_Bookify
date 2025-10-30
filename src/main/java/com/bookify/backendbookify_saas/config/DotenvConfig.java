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

        // Log a message to indicate successful loading of environment variables
        System.out.println("         =========================================================");
        System.out.println("        |                                                         |");
        System.out.println("        |         ✅ Dotenv loaded and system properties.         |");
        System.out.println("        |                                                         |");
        System.out.println("         =========================================================");
    }
}
