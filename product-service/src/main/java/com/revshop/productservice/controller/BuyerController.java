package com.revshop.productservice.controller;

import com.revshop.productservice.model.Product;
import com.revshop.productservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buyer/products")
public class BuyerController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> browseByCategory(@PathVariable String categoryName) {
        List<Product> products = productService.browseByCategory(categoryName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }

    @GetMapping("/name/{productName}")
    public Product getProductDetailsByName(@PathVariable String productName) {
        return productService.getProductDetailsByName(productName);
    }
}
