package com.revshop.cartservice.controller;

import com.revshop.cartservice.dto.CartItemDTO;
import com.revshop.cartservice.dto.CartResponseDTO;
import com.revshop.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/buyer/cart")
@RequiredArgsConstructor
public class BuyerCartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        Long productId = Long.valueOf(request.get("productId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(cartService.getCartResponse(userId));
    }

    @GetMapping("/view")
    public ResponseEntity<CartResponseDTO> viewCart(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(cartService.getCartResponse(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponseDTO> updateCartItem(
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") Long userId) {
        Long cartItemId = Long.valueOf(request.get("cartItemId").toString());
        Integer quantity = Integer.valueOf(request.get("quantity").toString());
        
        cartService.updateCartItemQuantity(cartItemId, quantity);
        return ResponseEntity.ok(cartService.getCartResponse(userId));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<CartResponseDTO> deleteCartItem(
            @PathVariable Long cartItemId,
            @RequestHeader("X-User-Id") Long userId) {
        cartService.deleteCartItem(cartItemId);
        return ResponseEntity.ok(cartService.getCartResponse(userId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok("Cart cleared successfully");
    }
}
