package com.revshop.userservice.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @GetMapping("/buyer/dashboard")
    public String buyerAccess() {
        return "Buyer Dashboard Accessed";
    }

    @GetMapping("/seller/dashboard")
    public String sellerAccess() {
        return "Seller Dashboard Accessed";
    }
}
