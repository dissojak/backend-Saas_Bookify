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
            "/api/v1/businesses/{businessId}/staffMembers",
            // Public endpoint to list services for a business (context path /api is prefixed at runtime)
            "/api/v1/businesses/{businessId}/services",
            // Public endpoint to list images for a business
            "/api/v1/businesses/{businessId}/images",
            "/v1/businesses/{businessId}/images",
            "/api/v1/staff/{staffId}/services",
            // Make staff calendar public (both /v1 and /api/v1 variants will be detected by regex)
            "/v1/staff/{staffId}/calendar",
            "/api/v1/staff/{staffId}/calendar",
            // Make business search public
            "/v1/businesses/search",
            "/api/v1/businesses/search",
            // Make all business listings public (GET only)
            "/v1/businesses",
            "/v1/businesses/",
            "/api/v1/businesses",
            "/api/v1/businesses/",
            // Make all category listings public
            "/v1/categories",
            "/v1/categories/",
            "/api/v1/categories",
            "/api/v1/categories/",
            // Admin static pages
            "/LoginAdmin.html",
            "/Dashboard.html",
            "/AdminBusinesses.html",
            "/AdminCategories.html",
            "/AdminUsers.html",
            "/AdminModeration.html",
            "/api/LoginAdmin.html",
            "/api/Dashboard.html",
            "/api/AdminBusinesses.html",
            "/api/AdminCategories.html",
            "/api/AdminUsers.html",
            "/api/AdminModeration.html",
            // Admin API endpoints (GET only - stats/lists are public)
            "/v1/admin/stats",
            "/api/v1/admin/stats",
            "/v1/admin/businesses/all",
            "/api/v1/admin/businesses/all",
            "/v1/admin/ratings/flagged",
            "/api/v1/admin/ratings/flagged",
            "/v1/admin/users",
            "/api/v1/admin/users",
            // Health check endpoints (public)
            "/v1/health/",
            "/api/v1/health/",
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
        String matched = getMatchingPublicPrefix(requestPath, requestMethod);
        if (matched != null) {
            // Special-case: the staff availabilities pattern should be public ONLY for GET requests
            // and only when both 'from' and 'to' query parameters are present.
            if ("REGEX:/v1/staff/{id}/availabilities".equals(matched)) {
                boolean isGet = "GET".equalsIgnoreCase(requestMethod);
                String fromParam = request.getParameter("from");
                String toParam = request.getParameter("to");
                boolean hasRangeParams = fromParam != null && !fromParam.isBlank() && toParam != null && !toParam.isBlank();
                if (isGet && hasRangeParams) {
                    System.out.println("✓ Public URL - Skipping JWT authentication (matched: '" + matched + "') [GET with from/to]");
                    filterChain.doFilter(request, response);
                    return;
                }
                // If it's the availabilities pattern but not a GET with both params, do NOT skip authentication
            }

            // Special-case: staff calendar should be public for GET requests (no params required)
            if ("REGEX:/v1/staff/{id}/calendar".equals(matched)) {
                boolean isGet = "GET".equalsIgnoreCase(requestMethod);
                if (isGet) {
                    System.out.println("✓ Public URL - Skipping JWT authentication (matched: '" + matched + "') [GET]");
                    filterChain.doFilter(request, response);
                    return;
                }
                // non-GET requests require authentication
            }

            // Special-case: business search should be public for GET requests (query param name provided by client)
            if ("REGEX:/v1/businesses/search".equals(matched)) {
                boolean isGet = "GET".equalsIgnoreCase(requestMethod);
                if (isGet) {
                    System.out.println("✓ Public URL - Skipping JWT authentication (matched: '" + matched + "') [GET]");
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // Fallback: existing literal public prefixes or other regex that should be fully public
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
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
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
     * Now also takes HTTP method into account for endpoints that should only be public for GET
     */
    private String getMatchingPublicPrefix(String requestPath, String requestMethod) {
        boolean isGet = "GET".equalsIgnoreCase(requestMethod);
        
        // First check parameterized endpoints using regexes - these must be matched before literal prefix checks
        String staffServicesRegex = "^/((api/)?)v1/staff/\\d+/services/?$";
        if (requestPath.matches(staffServicesRegex)) return "REGEX:/v1/staff/{id}/services";

        // business services listing - ONLY public for GET requests
        String businessServicesRegex = "^/((api/)?)v1/businesses/\\d+/services/?$";
        if (requestPath.matches(businessServicesRegex) && isGet) return "REGEX:/v1/businesses/{id}/services";

        // business staff members
        String staffRegex = "^/((api/)?)v1/businesses/\\d+/staffMembers/?$";
        if (requestPath.matches(staffRegex)) return "REGEX:/v1/businesses/{id}/staffMembers";

        // business images listing (public)
        String businessImagesRegex = "^/((api/)?)v1/businesses/\\d+/images/?$";
        if (requestPath.matches(businessImagesRegex)) return "REGEX:/v1/businesses/{id}/images";

        // staff availabilities listing
        String staffAvailabilitiesRegex = "^/((api/)?)v1/staff/\\d+/availabilities/?$";
        if (requestPath.matches(staffAvailabilitiesRegex)) return "REGEX:/v1/staff/{id}/availabilities";

        // staff calendar
        String staffCalendarRegex = "^/((api/)?)v1/staff/\\d+/calendar/?$";
        if (requestPath.matches(staffCalendarRegex)) return "REGEX:/v1/staff/{id}/calendar";

        // business search
        String businessSearchRegex = "^/((api/)?)v1/businesses/search/?$";
        if (requestPath.matches(businessSearchRegex)) return "REGEX:/v1/businesses/search";

        // Fallback: check literal public prefixes (static endpoints)
        for (String prefix : PUBLIC_URLS) {
            if (requestPath.startsWith(prefix)) return prefix;
        }

        return null;
    }
}
