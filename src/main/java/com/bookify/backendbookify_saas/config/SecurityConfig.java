package com.bookify.backendbookify_saas.config;

import com.bookify.backendbookify_saas.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import org.springframework.http.HttpMethod;

/**
 * Configuration de sécurité pour l'application
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                // Public auth endpoints
                                "/v1/auth/signup",
                                "/v1/auth/login",
                                "/v1/auth/refresh",
                                "/v1/auth/activate",
                                "/v1/auth/forgot-password",
                                "/v1/auth/reset-password",
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/activate",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/reset-password",
                                "/api/v1/businesses/{businessId}/staffMembers",
                                "/api/v1/staff/{staffId}/services",
                                // Health check endpoints (public)
                                "/v1/health/**",
                                "/api/v1/health/**",
                                // Swagger/OpenAPI - include both default and custom paths
                                "/v3/api-docs/**",
                                "/api/v3/api-docs/**",
                                "/api/v3/api-docs/swagger-config",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/api/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()
                        // Explicit GET permit for staff services listing (guarantee it is public)
                        .requestMatchers(HttpMethod.GET, "/api/v1/staff/*/services", "/v1/staff/*/services").permitAll()
                        // TEMP: Broad permit for staff GET endpoints (helps diagnose 403). Narrow this after verification.
                        .requestMatchers(HttpMethod.GET, "/api/v1/staff/**", "/v1/staff/**").permitAll()
                        // Explicit GET permit for staff calendar (public)
                        .requestMatchers(HttpMethod.GET, "/api/v1/staff/*/calendar", "/v1/staff/*/calendar").permitAll()
                        // Explicit GET permit for business search (public)
                        .requestMatchers(HttpMethod.GET, "/api/v1/businesses/search", "/v1/businesses/search").permitAll()
                        // Explicit GET permit for staff availabilities listing (public endpoint with from/to)
                        .requestMatchers(HttpMethod.GET, "/api/v1/staff/*/availabilities", "/v1/staff/*/availabilities", "/api/v1/staff/{staffId}/availabilities", "/v1/staff/{staffId}/availabilities").permitAll()
                        // Explicit GET permit for business services listing (public endpoint)
                        .requestMatchers(HttpMethod.GET, "/api/v1/businesses/*/services", "/v1/businesses/*/services").permitAll()
                        // Make the admin login page public
                        .requestMatchers(HttpMethod.GET, "/LoginAdmin.html").permitAll()
                        // Make common static resources public (CSS/JS/images/etc.)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/static/**",
                                "/assets/**",
                                // Note: removed patterns like '/**/*.css' because PathPattern (used by Spring 6) rejects '**' followed by more data
                                "/favicon.ico"
                        ).permitAll()
                        // TEMPORARILY ALLOW ALL CATEGORY & BUSINESSES ENDPOINTS FOR DEBUGGING
                        .requestMatchers(
                                HttpMethod.GET,
                                "/v1/categories/**",
                                "/api/v1/categories/**",
                                // Allow public businesses listing (GET only)
                                "/v1/businesses/**",
                                "/api/v1/businesses/**"
                        ).permitAll()
                        // Allow public access to staff bookings for slot filtering (read-only)
                        .requestMatchers(
                                HttpMethod.GET,
                                "/v1/bookings/staff/*",
                                "/api/v1/bookings/staff/*",
                                "/v1/bookings/staff/*/date/*",
                                "/api/v1/bookings/staff/*/date/*"
                        ).permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*")); // à restreindre en prod
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
