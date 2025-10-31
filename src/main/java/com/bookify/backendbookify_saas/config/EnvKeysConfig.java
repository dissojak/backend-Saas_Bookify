package com.bookify.backendbookify_saas.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

/**
 * Component class for managing environment variable keys.
 * This class uses the Dotenv library to load environment variables
 * and provides constants for accessing specific keys.
 */
@Component
public class EnvKeysConfig {

    // Load environment variables from the .env file
    private static final Dotenv dotenv = Dotenv.load();

    /**
     * API key for Gemini integration.
     * This key is loaded from the environment variables.
     */
    public static final String GEMINI_API_KEY = dotenv.get("GEMINI_API_KEY");

}
