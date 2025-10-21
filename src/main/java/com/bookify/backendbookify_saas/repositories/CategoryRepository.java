package com.bookify.backendbookify_saas.repositories;

import com.bookify.backendbookify_saas.models.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // ...existing code...
}
