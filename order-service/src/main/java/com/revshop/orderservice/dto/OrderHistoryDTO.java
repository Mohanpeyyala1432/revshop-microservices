package com.revshop.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryDTO {
    private Long orderId;
    private String status;
    private Double totalAmount;
    private String shippingAddress;
    private String billingAddress;
    private List<OrderItemDTO> items;
    private String paymentMethod;
    private String transactionId;
    private String paymentDate;
}
