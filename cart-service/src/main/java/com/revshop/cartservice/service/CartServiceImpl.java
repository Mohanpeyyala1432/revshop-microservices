package com.revshop.cartservice.service;

import com.revshop.cartservice.client.ProductClient;
import com.revshop.cartservice.dto.CartItemDTO;
import com.revshop.cartservice.dto.CartResponseDTO;
import com.revshop.cartservice.dto.ProductDTO;
import com.revshop.cartservice.model.*;
import com.revshop.cartservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductClient productClient;

    public Cart getCartByUserId(Long userId) {
        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart = cartRepository.save(cart);
        }
        return cart;
    }

    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartByUserId(userId);
        
        // Fetch product details from Product Service
        ProductDTO product = productClient.getProductById(productId);
        
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setProductName(product.getProductName());
        item.setPrice(product.getPrice());
        cartItemRepository.save(item);
        
        // Calculate and update total amount
        cart = cartRepository.findByUserId(userId);
        double totalAmount = cart.getItems().stream()
            .mapToDouble(i -> i.getPrice() * i.getQuantity())
            .sum();
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
        
        return cart;
    }

    @Transactional
    public void clearCart(Long userId) {
        System.out.println("=== CLEAR CART CALLED FOR USER: " + userId + " ===");
        Cart cart = cartRepository.findByUserId(userId);
        if (cart != null) {
            System.out.println("Cart found with ID: " + cart.getCartId() + ", Items count: " + (cart.getItems() != null ? cart.getItems().size() : 0));
            // Delete all cart items by cart ID
            cartItemRepository.deleteByCartCartId(cart.getCartId());
            System.out.println("Cart items deleted for cart ID: " + cart.getCartId());
            // Then delete the cart
            cartRepository.delete(cart);
            System.out.println("Cart deleted successfully for user: " + userId);
        } else {
            System.out.println("No cart found for user: " + userId);
        }
    }

    public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new RuntimeException("Cart item not found"));
        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }

    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public CartResponseDTO getCartResponse(Long userId) {
        Cart cart = getCartByUserId(userId);
        
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return new CartResponseDTO(List.of(), 0.0);
        }
        
        List<CartItemDTO> items = cart.getItems().stream()
            .map(item -> new CartItemDTO(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity()
            ))
            .collect(Collectors.toList());
        
        double totalAmount = items.stream()
            .mapToDouble(i -> i.getPrice() * i.getQuantity())
            .sum();
        
        return new CartResponseDTO(items, totalAmount);
    }
}
