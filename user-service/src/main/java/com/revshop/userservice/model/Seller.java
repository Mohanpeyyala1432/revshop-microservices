package com.revshop.userservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "sellers")
@Data
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sellerId;

    private String sellerName;
    private String email;
}
