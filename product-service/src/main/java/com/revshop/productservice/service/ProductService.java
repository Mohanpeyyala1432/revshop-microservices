package com.revshop.productservice.service;

import com.revshop.productservice.model.Product;
import java.util.List;

public interface ProductService {
    Product addProduct(Product product, Long sellerId);
    Product updateProduct(Long productId, Product updatedProduct, Long sellerId);
    void deleteProduct(Long productId, Long sellerId);
    Product getProductById(Long productId);
    List<Product> getSellerInventory(Long sellerId);
    List<Product> getLowStockProducts();
    List<Product> getLowStockProductsBySeller(Long sellerId);
    int getLowStockCountBySeller(Long sellerId);
    List<Product> getAllProducts();
    int getLowStockCount();
    List<Product> getLowStockProducts(Integer threshold);
    List<Product> browseByCategory(String categoryName);
    List<Product> searchProducts(String keyword);
    Product getProductDetailsByName(String productName);
}
