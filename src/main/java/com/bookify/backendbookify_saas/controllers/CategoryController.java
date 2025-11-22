package com.bookify.backendbookify_saas.controllers;

import com.bookify.backendbookify_saas.models.dtos.CategoryCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Category;
import com.bookify.backendbookify_saas.repositories.UserRepository;
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

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category endpoints")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepository userRepository;

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
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new category (admin only)")
    public ResponseEntity<Category> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            Authentication authentication
    ) {
        String creatorEmail = authentication.getName();
        Category created = categoryService.createCategory(request, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
