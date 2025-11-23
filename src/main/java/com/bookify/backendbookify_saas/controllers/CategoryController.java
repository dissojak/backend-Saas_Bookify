package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.CategoryCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Category;
import com.bookify.backendbookify_saas.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category endpoints")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/test")
    @Operation(summary = "Test endpoint - completely public")
    public ResponseEntity<String> testEndpoint() {
        System.out.println("========================================");
        System.out.println("TEST ENDPOINT HIT!");
        System.out.println("========================================");
        return ResponseEntity.ok("Test endpoint works!");
    }

    @PostMapping("/test")
    @Operation(summary = "Test POST endpoint - completely public")
    public ResponseEntity<String> testPostEndpoint(@RequestBody(required = false) String body) {
        System.out.println("========================================");
        System.out.println("TEST POST ENDPOINT HIT!");
        System.out.println("Body: " + body);
        System.out.println("========================================");
        return ResponseEntity.ok("Test POST endpoint works!");
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.findAll();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/preview")
    @Operation(summary = "Get preview of categories (first 12)")
    public ResponseEntity<List<Category>> getPreviewCategories() {
        List<Category> categories = categoryService.findPreview(12);
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')") // Temporarily disabled for debugging
    @Operation(summary = "Create a new category (admin only)")
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            Authentication authentication
    ) {
        System.out.println("========================================");
        System.out.println("CREATE CATEGORY ENDPOINT REACHED!");
        System.out.println("Authentication object: " + authentication);
        System.out.println("Authentication.getName(): " + authentication.getName());
        System.out.println("Authentication.getPrincipal(): " + authentication.getPrincipal());
        System.out.println("Authentication.getAuthorities(): " + authentication.getAuthorities());
        System.out.println("========================================");

        String userId = authentication.getName(); // Returns userId (from JWT subject)
        System.out.println("USER ID = " + userId);
        System.out.println("USER AUTHORITIES = " + authentication.getAuthorities());
        Category created = categoryService.createCategory(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/create-no-auth")
    @Operation(summary = "Create category WITHOUT any authentication - FOR TESTING ONLY")
    public ResponseEntity<Map<String, String>> createCategoryNoAuth(@RequestBody CategoryCreateRequest request) {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║ NO-AUTH ENDPOINT HIT!!!!                 ║");
        System.out.println("║ Category name: " + request.getName() + "                  ║");
        System.out.println("╚══════════════════════════════════════════╝");
        return ResponseEntity.ok(Map.of(
            "message", "NO-AUTH endpoint works! Category would be: " + request.getName(),
            "description", request.getDescription() != null ? request.getDescription() : "none"
        ));
    }
}
