package com.revshop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewResponseDTO {
    private String userName;
    private int rating;
    private String comment;
}
