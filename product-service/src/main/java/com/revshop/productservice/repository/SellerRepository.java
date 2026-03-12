package com.revshop.productservice.repository;

import com.revshop.productservice.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}

