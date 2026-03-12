package com.revshop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerOrderViewDTO {

    private Long orderId;

    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;

    private String productName;
    private Integer quantity;
    private Double price;

    private Double totalAmount;
}
