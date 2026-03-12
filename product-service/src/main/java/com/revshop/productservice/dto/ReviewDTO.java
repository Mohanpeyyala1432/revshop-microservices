package com.revshop.productservice.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long userId;
    private Integer rating;
    private String comment;
    private String userName;
    private LocalDateTime createdAt;
}
