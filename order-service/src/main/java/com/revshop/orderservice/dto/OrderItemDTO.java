package com.revshop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDTO {
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
}
