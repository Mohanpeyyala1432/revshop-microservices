package com.revshop.productservice.repository;

import com.revshop.productservice.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
