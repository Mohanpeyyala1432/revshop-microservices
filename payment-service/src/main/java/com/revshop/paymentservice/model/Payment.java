package com.revshop.paymentservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(name = "order_id")
    private Long orderId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "gateway_response")
    private String gatewayResponse;

    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    @Column(name = "user_id")
    private Long userId;
}

