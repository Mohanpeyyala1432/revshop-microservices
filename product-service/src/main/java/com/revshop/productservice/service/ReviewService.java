package com.revshop.productservice.service;

import com.revshop.productservice.client.OrderClient;
import com.revshop.productservice.dto.ReviewDTO;
import com.revshop.productservice.dto.ReviewSummaryDTO;
import com.revshop.productservice.model.Review;
import com.revshop.productservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderClient orderClient;

    public ReviewDTO addReview(Long productId, ReviewDTO reviewDTO) {
        Long userId = reviewDTO.getUserId();
        
        boolean hasPurchased;
        try {
            hasPurchased = orderClient.hasUserPurchasedProduct(userId, productId);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to verify purchase. Please try again later.");
        }
        
        if (!hasPurchased) {
            throw new IllegalStateException("You can only review products you have purchased");
        }
        
        boolean alreadyReviewed = reviewRepository.existsByUserIdAndProductId(userId, productId);
        if (alreadyReviewed) {
            throw new IllegalStateException("You have already reviewed this product");
        }
        
        Review review = new Review();
        review.setProductId(productId);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setUserName(reviewDTO.getUserName());
        review.setUserId(userId);
        
        Review saved = reviewRepository.save(review);
        
        reviewDTO.setCreatedAt(saved.getCreatedAt());
        return reviewDTO;
    }

    public ReviewSummaryDTO getReviewsByProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        
        List<ReviewDTO> reviewDTOs = reviews.stream()
            .map(r -> {
                ReviewDTO dto = new ReviewDTO();
                dto.setRating(r.getRating());
                dto.setComment(r.getComment());
                dto.setUserName(r.getUserName());
                dto.setCreatedAt(r.getCreatedAt());
                return dto;
            })
            .collect(Collectors.toList());
        
        double avgRating = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
        
        return new ReviewSummaryDTO(avgRating, reviews.size(), reviewDTOs);
    }
}
