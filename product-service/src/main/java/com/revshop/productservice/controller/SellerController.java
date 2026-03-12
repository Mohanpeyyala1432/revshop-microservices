package com.revshop.productservice.controller;

import com.revshop.productservice.model.Seller;
import com.revshop.productservice.service.SellerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private static final Logger logger = LoggerFactory.getLogger(SellerController.class);

    @Autowired
    private SellerService sellerService;

    @GetMapping("/{id}")
    public Seller getSeller(@PathVariable Long id) {
        logger.info("Request received to fetch seller with ID: {}", id);
        Seller seller = sellerService.getSellerById(id);
        logger.info("Fetched seller: {}", seller);
        return seller;
    }
}

