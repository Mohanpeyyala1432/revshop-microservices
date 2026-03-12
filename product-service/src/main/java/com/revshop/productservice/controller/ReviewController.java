package com.revshop.productservice.controller;

import com.revshop.productservice.dto.ReviewDTO;
import com.revshop.productservice.dto.ReviewSummaryDTO;
import com.revshop.productservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyer/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{productId}")
    public ReviewDTO addReview(@PathVariable Long productId, @RequestBody ReviewDTO reviewDTO, 
                               @RequestHeader("X-User-Id") Long userId,
                               @RequestHeader("X-User-Name") String userName) {
        reviewDTO.setUserId(userId);
        reviewDTO.setUserName(userName);
        return reviewService.addReview(productId, reviewDTO);
    }

    @GetMapping("/{productId}")
    public ReviewSummaryDTO getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsByProduct(productId);
    }
    
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
