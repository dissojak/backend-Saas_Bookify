package com.bookify.backendbookify_saas.services;

import com.bookify.backendbookify_saas.models.dtos.CategoryCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    List<Category> findPreview(int limit);

    Category createCategory(CategoryCreateRequest request, String creatorUserId);
}
