package com.revshop.orderservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "seller_id")
    private Long sellerId;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private
    OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private String shippingAddress;
    private String billingAddress;
}


