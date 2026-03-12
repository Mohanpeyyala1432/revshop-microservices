package com.revshop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerOrderViewDTO {
    private Long orderId;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private String productName;
    private Integer quantity;
    private Double price;
    private Double totalAmount;
    private String status;
}
