package com.revshop.userservice.controller;

import com.revshop.userservice.model.Seller;
import com.revshop.userservice.service.SellerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
@RequiredArgsConstructor
public class SellerController {

    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
    private final SellerService sellerService;

    @GetMapping("/{id}")
    public Seller getSeller(@PathVariable Long id) {
        logger.info("Request received to fetch seller with ID: {}", id);
        Seller seller = sellerService.getSellerById(id);
        logger.info("Fetched seller: {}", seller);
        return seller;
    }
}
