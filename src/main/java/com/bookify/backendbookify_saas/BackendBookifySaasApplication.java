package com.bookify.backendbookify_saas;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendBookifySaasApplication {

    static Dotenv dotenv = Dotenv.configure().load();
    private static final String GEMINI_API_KEY = dotenv.get("GEMINI_API_KEY");
    private static final String SPRING_MAIL_PASSWORD = dotenv.get("SPRING_MAIL_PASSWORD") ;

    public static void main(String[] args) {
        SpringApplication.run(BackendBookifySaasApplication.class, args);
    }
    private static final Logger logger = LoggerFactory.getLogger(BackendBookifySaasApplication.class);
    @Bean
    public CommandLineRunner commandLineRunner(Environment environment) {

        return args -> {
            String port = environment.getProperty("server.port", "8088");
            String contextPath = environment.getProperty("server.servlet.context-path", "");

            System.out.println("\n----------------------------------------------------------");
            System.out.println("🚀 Bookify SaaS Server is running!");
            System.out.println("    📡 Local:   http://localhost:" + port + contextPath);
            try {
                String hostAddress = java.net.InetAddress.getLocalHost().getHostAddress();
                System.out.println("    🌐 Network: http://" + hostAddress + ":" + port + contextPath);
            } catch (Exception e) {
                // Ignore network address error
            }
            System.out.println("    ⚙️  API Documentation: http://localhost:" + port + contextPath + "/swagger-ui.html");
            System.out.println("SPRING_MAIL_PASSWORD from .env: " + SPRING_MAIL_PASSWORD);
            System.out.println("GEMINI_API_KEY from .env: " + GEMINI_API_KEY);
            System.out.println("----------------------------------------------------------\n");
        };
    }
}