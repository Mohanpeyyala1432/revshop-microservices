package com.revshop.orderservice.controller;

import com.revshop.orderservice.dto.SellerOrderViewDTO;
import com.revshop.orderservice.model.OrderStatus;
import com.revshop.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/seller/orders")
@RequiredArgsConstructor
public class SellerOrderController {

    private final OrderService orderService;

    @GetMapping("/all")
    public ResponseEntity<List<SellerOrderViewDTO>> getSellerOrders(@RequestHeader("X-User-Id") Long sellerId) {
        List<SellerOrderViewDTO> orders = orderService.getOrdersForSeller(sellerId);
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/update-status/{orderId}")
    public ResponseEntity<String> updateOrderStatusBySeller(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        orderService.updateOrderStatusBySeller(orderId, status);
        return ResponseEntity.ok("Order " + orderId + " status updated to " + status);
    }
}
