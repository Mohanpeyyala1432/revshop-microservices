package com.revshop.productservice.service;

import com.revshop.productservice.exception.CategoryNotFoundException;
import com.revshop.productservice.exception.ProductNotFoundException;
import com.revshop.productservice.model.Category;
import com.revshop.productservice.model.Product;
import com.revshop.productservice.repository.CategoryRepository;
import com.revshop.productservice.repository.ProductRepository;
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
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Electronics");

        product = new Product();
        product.setProductId(1L);
        product.setProductName("Test Product");
        product.setDescription("Description");
        product.setPrice(100.0);
        product.setMrp(120.0);
        product.setDiscount(20.0);
        product.setQuantity(50);
        product.setLowStockThreshold(10);
        product.setIsActive(true);
        product.setSellerId(1L);
        product.setCategory(category);
    }

    @Test
    void addProduct_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.addProduct(product, 1L);

        assertNotNull(result);
        assertEquals(1L, result.getSellerId());
        assertTrue(result.getIsActive());
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        
        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Product");
        updatedProduct.setCategory(category);
        
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct, 1L);

        assertNotNull(result);
        assertEquals("Updated Product", result.getProductName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_Unauthorized_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product updatedProduct = new Product();
        updatedProduct.setProductName("Updated Product");

        assertThrows(RuntimeException.class, () -> productService.updateProduct(1L, updatedProduct, 2L)); // Wrong sellerId
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L, 1L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_Unauthorized_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () -> productService.deleteProduct(1L, 2L)); // Wrong sellerId
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getSellerInventory_Success() {
        when(productRepository.findBySellerId(1L)).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getSellerInventory(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getSellerId());
    }

    @Test
    void getLowStockProducts_Success() {
        when(productRepository.findLowStockProducts()).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getLowStockProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getLowStockProductsBySeller_Success() {
        product.setQuantity(5); // Below threshold of 10
        when(productRepository.findBySellerId(1L)).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getLowStockProductsBySeller(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllProducts_Success() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<Product> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void browseByCategory_Success() {
        when(categoryRepository.findFirstByCategoryNameIgnoreCase("Electronics")).thenReturn(Optional.of(category));
        when(productRepository.findByCategory(category)).thenReturn(Arrays.asList(product));

        List<Product> result = productService.browseByCategory("Electronics");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void browseByCategory_CategoryNotFound_ThrowsException() {
        when(categoryRepository.findFirstByCategoryNameIgnoreCase("Unknown")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> productService.browseByCategory("Unknown"));
    }

    @Test
    void searchProducts_Success() {
        when(productRepository.findByProductNameContainingIgnoreCase("Test")).thenReturn(Arrays.asList(product));

        List<Product> result = productService.searchProducts("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void searchProducts_NotFound_ThrowsException() {
        when(productRepository.findByProductNameContainingIgnoreCase("Unknown")).thenReturn(Arrays.asList());

        assertThrows(ProductNotFoundException.class, () -> productService.searchProducts("Unknown"));
    }
}
