package com.revshop.orderservice.controller;

import com.revshop.orderservice.dto.*;
import com.revshop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buyer/order")
@RequiredArgsConstructor
public class BuyerOrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        System.out.println("=== CONTROLLER: Checkout called for userId: " + userId);
        try {
            CheckoutResponseDTO response = orderService.checkout(userId, request);
            System.out.println("=== CONTROLLER: Checkout completed");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/buy-now")
    public ResponseEntity<?> buyNow(
            @RequestBody BuyNowRequestDTO request,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            CheckoutResponseDTO response = orderService.buyNow(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(503)
                .body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@RequestHeader("X-User-Id") Long userId) {
        List<OrderHistoryDTO> history = orderService.getOrderHistory(userId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        String msg = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(msg);
    }
}
