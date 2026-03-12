package com.revshop.productservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    private static final Logger logger = LoggerFactory.getLogger(ProductImageController.class);

    @GetMapping("/image/{fileName}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable String fileName) throws IOException {
        Path imagePath = Paths.get("uploads/" + fileName);
        
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
}
