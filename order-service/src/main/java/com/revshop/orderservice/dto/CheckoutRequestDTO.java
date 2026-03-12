package com.revshop.orderservice.dto;

import lombok.Data;

@Data
public class CheckoutRequestDTO {
    private String shippingAddress;
    private String billingAddress;
    private String contactNumber;
    private String paymentMethod;
}
