package com.bookify.backendbookify_saas.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT authentication filter to validate tokens in requests
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // List of public endpoints that don't require authentication
    private static final List<String> PUBLIC_URLS = Arrays.asList(
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
            // Swagger/OpenAPI
            "/v3/api-docs/",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/swagger-resources/",
            "/webjars/"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String requestPath = request.getRequestURI();
        final String requestMethod = request.getMethod();

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║ JWT FILTER - REQUEST RECEIVED");
        System.out.println("║ Method: " + requestMethod);
        System.out.println("║ Path: " + requestPath);
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        // Skip JWT authentication for public endpoints
        String matched = getMatchingPublicPrefix(requestPath);
        if (matched != null) {
            System.out.println("✓ Public URL - Skipping JWT authentication (matched: '" + matched + "')");
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization header: " + (authHeader != null ? "Present (Bearer...)" : "Missing"));

        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("✗ No Bearer token - Passing to Spring Security");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract JWT token from header
        final String jwt = authHeader.substring(7);
        System.out.println("✓ JWT Token extracted: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");

        // Extract subject (must be userId now)
        final String tokenSubject;
        try {
            tokenSubject = jwtService.extractUsername(jwt);
            System.out.println("✓ Token subject (userId) extracted: " + tokenSubject);
        } catch (Exception ex) {
            System.out.println("✗ FAILED to extract subject from token: " + ex.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or malformed token\"}");
            return;
        }

        // Subject MUST be numeric user id
        Long userId;
        try {
            userId = Long.parseLong(tokenSubject);
            System.out.println("✓ Parsed userId: " + userId);
        } catch (NumberFormatException ex) {
            System.out.println("✗ FAILED to parse userId (not a number): " + tokenSubject);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Token subject must be user id\"}");
            return;
        }

        // Check if user is not already authenticated
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("→ Loading user details for ID: " + userId);

            UserDetails userDetails;
            try {
                userDetails = this.userDetailsService.loadUserByUsername(String.valueOf(userId));
                System.out.println("✓ User loaded successfully");
                System.out.println("  Username: " + userDetails.getUsername());
                System.out.println("  Authorities: " + userDetails.getAuthorities());
            } catch (Exception ex) {
                System.out.println("✗ FAILED to load user: " + ex.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"User not found\"}");
                return;
            }

            System.out.println("=== JWT AUTHENTICATION DEBUG ===");
            System.out.println("User ID from token: " + userId);
            System.out.println("UserDetails username: " + userDetails.getUsername());
            System.out.println("UserDetails authorities: " + userDetails.getAuthorities());
            System.out.println("================================");

            // Validate token by subject (userId)
            boolean valid = jwtService.isTokenValidForSubject(jwt, String.valueOf(userId));
            System.out.println("Token validation result: " + (valid ? "VALID ✓" : "INVALID ✗"));

            if (!valid) {
                System.out.println("✗ Token validation FAILED - Returning 401");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("✓ Authentication set in SecurityContext");
        }

        System.out.println("→ Passing request to next filter in chain...");
        filterChain.doFilter(request, response);
        System.out.println("✓ Request completed successfully");
    }

    /**
     * Return the matching public prefix for debugging, or null if none
     */
    private String getMatchingPublicPrefix(String requestPath) {
        return PUBLIC_URLS.stream().filter(requestPath::startsWith).findFirst().orElse(null);
    }
}
