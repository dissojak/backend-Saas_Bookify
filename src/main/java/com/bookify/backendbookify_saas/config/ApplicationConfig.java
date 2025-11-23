package com.bookify.backendbookify_saas.config;

import com.bookify.backendbookify_saas.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration pour l'authentification dans l'application
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            logger.info("UserDetailsService called with username: {}", username);
            // Username is actually userId (from JWT subject which contains ID, not email)
            try {
                Long userId = Long.parseLong(username);
                logger.info("Parsed userId: {}", userId);

                return userRepository.findById(userId)
                        .map(u -> {
                            logger.info("User found - ID: {}, Role: {}", u.getId(), u.getRole());
                            String roleWithPrefix = "ROLE_" + u.getRole().name();
                            logger.info("Authority being set: {}", roleWithPrefix);

                            return new org.springframework.security.core.userdetails.User(
                                    String.valueOf(u.getId()), // Use ID as principal (this is what authentication.getName() returns)
                                    u.getPassword(),
                                    java.util.List.of(
                                            new org.springframework.security.core.authority.SimpleGrantedAuthority(roleWithPrefix)
                                    )
                            );
                        })
                        .orElseThrow(() -> {
                            logger.error("User not found with ID: {}", userId);
                            return new UsernameNotFoundException("User not found with ID: " + userId);
                        });
            } catch (NumberFormatException e) {
                logger.error("Invalid user identifier (not a number): {}", username);
                throw new UsernameNotFoundException("Invalid user identifier: " + username);
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
