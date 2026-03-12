package com.revshop.productservice.dto;

import lombok.Data;

@Data
public class ProductDTO {

    private Long productId;
    private String productName;
    private String description;

    private Double price;
    private Double mrp;
    private Double discount;

    private Integer quantity;
    private Integer lowStockThreshold;

    private String categoryName;
}


