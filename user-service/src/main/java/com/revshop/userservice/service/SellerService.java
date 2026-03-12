package com.revshop.userservice.service;

import com.revshop.userservice.model.Seller;
import com.revshop.userservice.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    public Seller getSellerById(Long id) {
        return sellerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Seller not found with ID: " + id));
    }
}
