package com.revshop.productservice.repository;

import com.revshop.productservice.model.Category;
import com.revshop.productservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellerId(Long sellerId);


    @Query("SELECT p FROM Product p WHERE p.quantity <= p.lowStockThreshold")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity <= p.lowStockThreshold")
    int countLowStockProducts();

    boolean existsByCategory_CategoryId(Long categoryId);


    List<Product> findByQuantityLessThan(Integer threshold);


    //  Search by keyword (name or description)
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    //find by product Name
    List<Product> findByProductNameContainingIgnoreCase(String keyword);

    Optional<Product> findByProductNameIgnoreCase(String productName);

    // Browse by category
    List<Product> findByCategory(Category category);

}


