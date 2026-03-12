package com.revshop.productservice.model;

import jakarta.persistence.*;
import lombok.Data;


    @Entity
    @Table(name = "products")
    @Data
    public class Product {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long productId;

        private Long sellerId;
        private String productName;
        private String description;
        private Double price;
        private Double mrp;
        private Double discount;
        private Integer quantity;
        private Integer lowStockThreshold;
        private Boolean isActive;

        private String imageName;

        @ManyToOne
        @JoinColumn(name = "category_id")
        private Category category;
    }



