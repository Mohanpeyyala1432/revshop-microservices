package com.revshop.cartservice.controller;

import com.revshop.cartservice.model.Cart;
import com.revshop.cartservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public Cart getCart(@PathVariable Long userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping("/add")
    public Cart addToCart(@RequestParam Long userId, @RequestParam Long productId, @RequestParam Integer quantity) {
        return cartService.addToCart(userId, productId, quantity);
    }

    @DeleteMapping("/clear/{userId}")
    public void clearCart(@PathVariable Long userId) {
        System.out.println("=== CartController.clearCart called for userId: " + userId + " ===");
        cartService.clearCart(userId);
        System.out.println("=== CartController.clearCart completed ===");
    }
}
