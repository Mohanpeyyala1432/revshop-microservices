package com.revshop.cartservice.service;

import com.revshop.cartservice.client.ProductClient;
import com.revshop.cartservice.dto.CartItemDTO;
import com.revshop.cartservice.dto.CartResponseDTO;
import com.revshop.cartservice.dto.ProductDTO;
import com.revshop.cartservice.model.Cart;
import com.revshop.cartservice.model.CartItem;
import com.revshop.cartservice.repository.CartItemRepository;
import com.revshop.cartservice.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private CartItem cartItem;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1L);
        cart.setUserId(1L);
        cart.setItems(new ArrayList<>());
        cart.setTotalAmount(0.0);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProductId(1L);
        cartItem.setQuantity(2);
        cartItem.setPrice(100.0);
        cartItem.setProductName("Test Product");
        cartItem.setCart(cart);

        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Test Product");
        productDTO.setPrice(100.0);
    }

    @Test
    void getCartByUserId_ExistingCart_ReturnsCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        Cart result = cartService.getCartByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void getCartByUserId_NewCart_CreatesAndReturnsCart() {
        when(cartRepository.findByUserId(1L)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getCartByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void addToCart_Success() {
        when(cartRepository.findByUserId(1L)).thenReturn(cart);
        when(productClient.getProductById(1L)).thenReturn(productDTO);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        
        // Setup after adding item
        cart.getItems().add(cartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(cart);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.addToCart(1L, 1L, 2);

        assertNotNull(result);
        assertEquals(200.0, result.getTotalAmount());
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
    }

    @Test
    void clearCart_CartExists_DeletesCartAndItems() {
        cart.getItems().add(cartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        cartService.clearCart(1L);

        verify(cartItemRepository).deleteByCartCartId(1L);
        verify(cartRepository).delete(cart);
    }

    @Test
    void clearCart_CartDoesNotExist_DoesNothing() {
        when(cartRepository.findByUserId(1L)).thenReturn(null);

        cartService.clearCart(1L);

        verify(cartItemRepository, never()).deleteByCartCartId(anyLong());
        verify(cartRepository, never()).delete(any(Cart.class));
    }

    @Test
    void updateCartItemQuantity_Success() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartService.updateCartItemQuantity(1L, 5);

        assertEquals(5, cartItem.getQuantity());
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void updateCartItemQuantity_ItemNotFound_ThrowsException() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> cartService.updateCartItemQuantity(1L, 5));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void deleteCartItem_Success() {
        cartService.deleteCartItem(1L);
        verify(cartItemRepository).deleteById(1L);
    }

    @Test
    void getCartResponse_WithItems_ReturnsDTO() {
        cart.getItems().add(cartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        CartResponseDTO result = cartService.getCartResponse(1L);

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(200.0, result.getTotalAmount());
        
        CartItemDTO dto = result.getItems().get(0);
        assertEquals(1L, dto.getCartItemId());
        assertEquals(1L, dto.getProductId());
        assertEquals("Test Product", dto.getProductName());
        assertEquals(100.0, dto.getPrice());
        assertEquals(2, dto.getQuantity());
    }

    @Test
    void getCartResponse_EmptyCart_ReturnsEmptyDTO() {
        when(cartRepository.findByUserId(1L)).thenReturn(cart);

        CartResponseDTO result = cartService.getCartResponse(1L);

        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0.0, result.getTotalAmount());
    }
}
