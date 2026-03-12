package com.revshop.productservice.service;

import com.revshop.productservice.exception.CategoryNotFoundException;
import com.revshop.productservice.model.Category;
import com.revshop.productservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");
        category.setDescription("Electronic items");
    }

    @Test
    void addCategory_Success() {
        when(categoryRepository.findFirstByCategoryNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.addCategory(category);

        assertNotNull(result);
        assertEquals("Electronics", result.getCategoryName());
        verify(categoryRepository).save(category);
    }

    @Test
    void addCategory_AlreadyExists_ThrowsException() {
        when(categoryRepository.findFirstByCategoryNameIgnoreCase("Electronics")).thenReturn(Optional.of(category));

        assertThrows(RuntimeException.class, () -> categoryService.addCategory(category));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void getAllCategories_Success() {
        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category));

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
    }

    @Test
    void updateCategory_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("New Electronics");
        updatedCategory.setDescription("New Description");
        
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(1L, updatedCategory);

        assertNotNull(result);
        assertEquals("New Electronics", result.getCategoryName());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void updateCategory_NotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.updateCategory(1L, category));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_Success() {
        doNothing().when(categoryRepository).deleteById(1L);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void getCategoryById_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getCategoryId());
    }

    @Test
    void getCategoryById_NotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> categoryService.getCategoryById(1L));
    }
}
