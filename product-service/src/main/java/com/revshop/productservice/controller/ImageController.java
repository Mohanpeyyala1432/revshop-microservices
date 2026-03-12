package com.revshop.productservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/uploads")
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String filename) {
        try {
            // Use absolute path to work with both command line and IntelliJ
            String uploadsDir = System.getProperty("user.dir") + "/uploads";
            logger.info("Looking for image: {} in directory: {}", filename, uploadsDir);
            Path filePath = Paths.get(uploadsDir).resolve(filename).normalize();
            logger.info("Full file path: {}", filePath.toAbsolutePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
