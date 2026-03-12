package com.revshop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ReviewSummaryDTO {
    private double averageRating;
    private int totalReviews;
    private List<ReviewDTO> reviews;
}
