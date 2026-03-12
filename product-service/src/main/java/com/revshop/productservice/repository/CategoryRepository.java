package com.revshop.productservice.repository;

import com.revshop.productservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

      Optional<Category> findByCategoryNameIgnoreCase(String categoryName);

      Optional<Category> findFirstByCategoryNameIgnoreCase(String categoryName);
}


