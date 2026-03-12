package com.revshop.cartservice.controller;

import com.revshop.cartservice.dto.WishlistDTO;
import com.revshop.cartservice.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/buyer/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{productId}")
    public String addToWishlist(@PathVariable Long productId, @RequestHeader("X-User-Id") Long userId) {
        wishlistService.addToWishlist(userId, productId);
        return "Product added to wishlist";
    }

    @DeleteMapping("/{productId}")
    public String removeFromWishlist(@PathVariable Long productId, @RequestHeader("X-User-Id") Long userId) {
        wishlistService.removeFromWishlist(userId, productId);
        return "Product removed from wishlist";
    }

    @GetMapping
    public List<WishlistDTO> getWishlist(@RequestHeader("X-User-Id") Long userId) {
        return wishlistService.getWishlist(userId);
    }
}
