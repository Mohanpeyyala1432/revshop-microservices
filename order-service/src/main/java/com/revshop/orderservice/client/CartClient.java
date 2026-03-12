package com.revshop.orderservice.client;

import com.revshop.orderservice.dto.CartResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service")
public interface CartClient {
    
    @GetMapping("/api/cart/{userId}")
    CartResponseDTO getCart(@PathVariable Long userId);
    
    @DeleteMapping("/api/cart/clear/{userId}")
    void clearCart(@PathVariable("userId") Long userId);
}
