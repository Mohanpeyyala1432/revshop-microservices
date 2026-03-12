package com.revshop.cartservice.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String imageName;
}
