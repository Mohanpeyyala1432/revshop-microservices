package com.revshop.productservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "order-service")
public interface OrderClient {
    @GetMapping("/api/orders/user/{userId}/product/{productId}/purchased")
    boolean hasUserPurchasedProduct(@PathVariable Long userId, @PathVariable Long productId);
}
