package com.revshop.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revshop.productservice.model.Product;
import com.revshop.productservice.model.Review;
import com.revshop.productservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/seller/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping("/add")
    public Product addProduct(@RequestBody Product product, @RequestHeader("X-User-Id") Long sellerId) {
        logger.info("Request to add product: {}", product);
        Product savedProduct = productService.addProduct(product, sellerId);
        logger.info("Product added successfully with ID: {}", savedProduct.getProductId());
        return savedProduct;
    }


    @PostMapping("/add-with-image")
    public Product addProductWithImage(
            @RequestParam("product") String productJson,
            @RequestParam("image") MultipartFile image,
            @RequestHeader("X-User-Id") Long sellerId) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        // Create uploads folder if not exists - use absolute path
        String uploadsDir = System.getProperty("user.dir") + "/uploads";
        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Unique file name
        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

        Files.copy(image.getInputStream(),
                uploadPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);

        product.setImageName(fileName);
        logger.info("Product image saved: {}", fileName);

        return productService.addProduct(product, sellerId);
    }

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable String fileName) throws IOException {
        String uploadsDir = System.getProperty("user.dir") + "/uploads";
        Path imagePath = Paths.get(uploadsDir, fileName);
        
        if (!Files.exists(imagePath)) {
            logger.error("Image not found: {}", fileName);
            return ResponseEntity.notFound().build();
        }
        
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String contentType = Files.probeContentType(imagePath);
        
        return ResponseEntity.ok()
                .header("Content-Type", contentType != null ? contentType : "image/jpeg")
                .body(imageBytes);
    }


    @PutMapping("/update/{id}")
    public Product updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            @RequestHeader("X-User-Id") Long sellerId) {
        logger.info("Request to update product with ID: {} by seller: {}", id, sellerId);
        Product updatedProduct = productService.updateProduct(id, product, sellerId);
        logger.info("Product updated successfully with ID: {}", updatedProduct.getProductId());
        return updatedProduct;
    }

    @PutMapping("/update-with-image/{id}")
    public Product updateProductWithImage(
            @PathVariable Long id,
            @RequestParam("product") String productJson,
            @RequestParam("image") MultipartFile image,
            @RequestHeader("X-User-Id") Long sellerId) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        String uploadsDir = System.getProperty("user.dir") + "/uploads";
        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
        Files.copy(image.getInputStream(),
                uploadPath.resolve(fileName),
                StandardCopyOption.REPLACE_EXISTING);

        product.setImageName(fileName);
        logger.info("Product image updated: {}", fileName);

        return productService.updateProduct(id, product, sellerId);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, @RequestHeader("X-User-Id") Long sellerId) {
        logger.info("Request to delete product with ID: {} by seller: {}", id, sellerId);
        productService.deleteProduct(id, sellerId);
        logger.info("Product deleted successfully with ID: {}", id);
        return "Product deleted successfully";
    }

    @GetMapping("/inventory/{sellerId}")
    public List<Product> getInventory(@PathVariable Long sellerId) {
        logger.info("Fetching inventory for seller ID: {}", sellerId);
        List<Product> inventory = productService.getSellerInventory(sellerId);
        logger.info("Found {} products for seller ID: {}", inventory.size(), sellerId);
        return inventory;
    }


    @GetMapping("/all")
    public List<Product> getAllProducts(@RequestHeader("X-User-Id") Long sellerId) {
        logger.info("Fetching all products for seller ID: {}", sellerId);
        return productService.getSellerInventory(sellerId);
    }

    @GetMapping("/low-stock")
    public List<Product> getLowStockProducts(@RequestHeader("X-User-Id") Long sellerId) {
        logger.info("Fetching low stock products for seller ID: {}", sellerId);
        return productService.getLowStockProductsBySeller(sellerId);
    }


    @GetMapping("/low-stock/count")
    public ResponseEntity<Integer> getLowStockCount(@RequestHeader("X-User-Id") Long sellerId) {
        int count = productService.getLowStockCountBySeller(sellerId);
        return ResponseEntity.ok(count);
    }

}

