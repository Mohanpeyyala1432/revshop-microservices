package com.revshop.cartservice.service;

import com.revshop.cartservice.dto.CartResponseDTO;
import com.revshop.cartservice.model.Cart;

public interface CartService {
    Cart getCartByUserId(Long userId);
    Cart addToCart(Long userId, Long productId, Integer quantity);
    void clearCart(Long userId);
    void updateCartItemQuantity(Long cartItemId, Integer quantity);
    void deleteCartItem(Long cartItemId);
    CartResponseDTO getCartResponse(Long userId);
}
