package com.revshop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResponseDTO {
    private Long orderId;
    private String status;
    private Double totalAmount;
}
