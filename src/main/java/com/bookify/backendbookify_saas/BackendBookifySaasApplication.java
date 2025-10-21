package com.bookify.backendbookify_saas;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BackendBookifySaasApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendBookifySaasApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(Environment environment) {
        return args -> {
            String port = environment.getProperty("server.port", "8080");
            String contextPath = environment.getProperty("server.servlet.context-path", "");

            System.out.println("\n----------------------------------------------------------");
            System.out.println("🚀 Bookify SaaS Server is running!");
            System.out.println("📡 Local:   http://localhost:" + port + contextPath);
            try {
                String hostAddress = java.net.InetAddress.getLocalHost().getHostAddress();
                System.out.println("🌐 Network: http://" + hostAddress + ":" + port + contextPath);
            } catch (Exception e) {
                // Ignore network address error
            }
            System.out.println("⚙️  API Documentation: http://localhost:" + port + contextPath + "/swagger-ui.html");
            System.out.println("----------------------------------------------------------\n");
        };
    }
}

/*
 * TODO: GitHub Copilot, please help me create the following structure:
 *
 * I need help setting up a Spring Boot project structure for a SaaS Booking System called "Bookify".
This is a multi-tenant application where businesses can register and offer appointment booking services to their customers.

Please create the following package structure with appropriate base files:

1. Main package structure:
   - com.bookify.backendbookify_saas.config (Security config, OpenAPI config, etc)
   - com.bookify.backendbookify_saas.controllers (REST controllers)
   - com.bookify.backendbookify_saas.models.entities (JPA entities)
   - com.bookify.backendbookify_saas.models.dtos (Request/Response DTOs)
   - com.bookify.backendbookify_saas.models.enums (Enum classes)
   - com.bookify.backendbookify_saas.repositories (Spring Data repositories)
   - com.bookify.backendbookify_saas.services (Service layer)
   - com.bookify.backendbookify_saas.services.impl (Service implementations)
   - com.bookify.backendbookify_saas.exceptions (Custom exceptions)
   - com.bookify.backendbookify_saas.security (JWT, authentication)
   - com.bookify.backendbookify_saas.utils (Utility classes)
   - com.bookify.backendbookify_saas.mappers (MapStruct mappers)
   - com.bookify.backendbookify_saas.ai (OpenAI integration)
   - com.bookify.backendbookify_saas.email (Email services)

2. Key entities to include:
   - User (id, email, password, role, etc.)
   - Business (id, name, description, ownerId, etc.)
   - Service (id, name, description, duration, price, businessId)
   - Booking (id, customerId, serviceId, businessId, startTime, endTime, status)
   - Customer (id, name, email, phone)
   - Review (id, bookingId, rating, comment)

3. Create initial configuration files:
   - Security configuration with JWT
   - Database configuration (MySQL)
   - OpenAPI documentation configuration
   - Email service configuration

4. Create base interfaces for services:
   - UserService
   - BusinessService
   - BookingService
   - CustomerService
   - ReviewService
   - EmailService
   - AIService (for OpenAI integration)

Please structure this following Spring Boot best practices with proper separation of concerns.
 */