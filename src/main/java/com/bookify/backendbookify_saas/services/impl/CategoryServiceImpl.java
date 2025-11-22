package com.bookify.backendbookify_saas.services.impl;

import com.bookify.backendbookify_saas.models.dtos.CategoryCreateRequest;
import com.bookify.backendbookify_saas.models.entities.Category;
import com.bookify.backendbookify_saas.models.entities.User;
import com.bookify.backendbookify_saas.repositories.CategoryRepository;
import com.bookify.backendbookify_saas.repositories.UserRepository;
import com.bookify.backendbookify_saas.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findPreview(int limit) {
        if (limit <= 0) {
            limit = 12;
        }
        return categoryRepository.findAll(PageRequest.of(0, limit)).getContent();
    }

    @Override
    @Transactional
    public Category createCategory(CategoryCreateRequest request, String creatorEmail) {
        if (request == null || request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name is required");
        }

        String name = request.getName().trim();
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with the same name already exists");
        }

        User creator = userRepository.findByEmail(creatorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found"));

        Category category = new Category();
        category.setName(name);
        category.setDescription(request.getDescription());
        category.setIcon(request.getIcon());
        category.setCreatedBy(creator);

        return categoryRepository.save(category);
    }
}
