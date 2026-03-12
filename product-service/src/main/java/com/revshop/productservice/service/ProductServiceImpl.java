package com.revshop.productservice.service;

import com.revshop.productservice.exception.*;
import com.revshop.productservice.model.*;
import com.revshop.productservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Product addProduct(Product product, Long sellerId) {
        product.setIsActive(true);
        product.setSellerId(sellerId);
        if (product.getCategory() != null && product.getCategory().getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, Product updatedProduct, Long sellerId) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!existingProduct.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized: You can only update your own products");
        }
        
        if (updatedProduct.getCategory() != null && updatedProduct.getCategory().getCategoryId() != null) {
            Category category = categoryRepository.findById(updatedProduct.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingProduct.setCategory(category);
        }
        
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setMrp(updatedProduct.getMrp());
        existingProduct.setDiscount(updatedProduct.getDiscount());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setLowStockThreshold(updatedProduct.getLowStockThreshold());
        existingProduct.setIsActive(updatedProduct.getIsActive());
        
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long productId, Long sellerId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own products");
        }

        productRepository.delete(product);
    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

    public List<Product> getSellerInventory(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> getLowStockProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId).stream()
                .filter(p -> p.getQuantity() <= p.getLowStockThreshold())
                .toList();
    }

    public int getLowStockCountBySeller(Long sellerId) {
        return (int) productRepository.findBySellerId(sellerId).stream()
                .filter(p -> p.getQuantity() <= p.getLowStockThreshold())
                .count();
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public int getLowStockCount() {
        return productRepository.findLowStockProducts().size();
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByQuantityLessThan(threshold);
    }

    public List<Product> browseByCategory(String categoryName) {
        Category category = categoryRepository.findFirstByCategoryNameIgnoreCase(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category not available"));
        
        List<Product> products = productRepository.findByCategory(category);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products in this category");
        }
        return products;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword);
        if (products.isEmpty()) {
            throw new ProductNotFoundException("No products found");
        }
        return products;
    }

    public Product getProductDetailsByName(String productName) {
        return productRepository.findByProductNameIgnoreCase(productName)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }
}
