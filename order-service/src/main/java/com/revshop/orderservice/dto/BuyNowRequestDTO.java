package com.revshop.orderservice.dto;

import lombok.Data;

@Data
public class BuyNowRequestDTO {
    private Long productId;
    private Integer quantity;
    private String shippingAddress;
    private String billingAddress;
}
