package com.revshop.orderservice.controller;

import com.revshop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class InternalOrderController {

    private final OrderService orderService;

    @GetMapping("/user/{userId}/product/{productId}/purchased")
    public boolean hasUserPurchasedProduct(@PathVariable Long userId, @PathVariable Long productId) {
        return orderService.hasUserPurchasedProduct(userId, productId);
    }
}
