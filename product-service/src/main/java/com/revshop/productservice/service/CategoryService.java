package com.revshop.productservice.service;

import com.revshop.productservice.model.Category;
import java.util.List;

public interface CategoryService {
    Category addCategory(Category category);
    List<Category> getAllCategories();
    Category updateCategory(Long categoryId, Category category);
    void deleteCategory(Long categoryId);
    Category getCategoryById(Long id);
}
