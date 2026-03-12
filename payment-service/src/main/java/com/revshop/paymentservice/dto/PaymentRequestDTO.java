package com.revshop.paymentservice.dto;

import com.revshop.paymentservice.model.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment type is required")
    private PaymentType type;

    @NotNull(message = "Amount is required")
    private Double amount;

    private String cardNumber;
    private String cardHolderName;
    private String cardExpiry;
    private String upiId;
    private String bankName;
}
