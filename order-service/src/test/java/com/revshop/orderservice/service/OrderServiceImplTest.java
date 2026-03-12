package com.revshop.orderservice.service;

import com.revshop.orderservice.client.*;
import com.revshop.orderservice.dto.*;
import com.revshop.orderservice.model.*;
import com.revshop.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartClient cartClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private OrderItem orderItem;
    private ProductDTO productDTO;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderId(1L);
        order.setUserId(1L);
        order.setSellerId(2L);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(100.0);
        order.setShippingAddress("Shipping Address");
        order.setBillingAddress("Billing Address");

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
        orderItem.setPrice(50.0);
        orderItem.setOrder(order);

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);
        order.setOrderItems(items);

        productDTO = new ProductDTO();
        productDTO.setProductId(1L);
        productDTO.setProductName("Test Product");
        productDTO.setPrice(50.0);
        productDTO.setSellerId(2L);

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setName("Test User");
        userDTO.setEmail("test@test.com");
    }

    @Test
    void createOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrdersByUserId_Success() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));

        List<Order> result = orderService.getOrdersByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void getOrdersForSeller_Success() {
        when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
        when(productClient.getProductById(1L)).thenReturn(productDTO);
        when(userClient.getUserById(1L)).thenReturn(userDTO);

        List<SellerOrderViewDTO> result = orderService.getOrdersForSeller(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals("Test User", result.get(0).getBuyerName());
    }

    @Test
    void updateOrderStatusBySeller_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.updateOrderStatusBySeller(1L, OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatusBySeller_CancelledOrder_ThrowsException() {
        order.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(RuntimeException.class, () -> 
            orderService.updateOrderStatusBySeller(1L, OrderStatus.DELIVERED));
    }

    @Test
    void checkout_Success() {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setShippingAddress("Shipping");
        request.setBillingAddress("Billing");

        CartResponseDTO cartDTO = new CartResponseDTO();
        CartItemDTO itemDTO = new CartItemDTO(1L, 2, 50.0);
        cartDTO.setItems(Arrays.asList(itemDTO));

        when(cartClient.getCart(1L)).thenReturn(cartDTO);
        when(productClient.getProductById(1L)).thenReturn(productDTO);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        CheckoutResponseDTO result = orderService.checkout(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("CONFIRMED", result.getStatus());
        verify(cartClient).clearCart(1L);
        verify(notificationClient, times(2)).sendNotification(anyLong(), anyString()); // buyer + seller
    }

    @Test
    void buyNow_Success() {
        BuyNowRequestDTO request = new BuyNowRequestDTO();
        request.setProductId(1L);
        request.setQuantity(2);
        request.setShippingAddress("Shipping");
        request.setBillingAddress("Billing");

        when(productClient.getProductById(1L)).thenReturn(productDTO);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        CheckoutResponseDTO result = orderService.buyNow(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals(100.0, result.getTotalAmount());
        verify(notificationClient, times(2)).sendNotification(anyLong(), anyString());
    }

    @Test
    void cancelOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(userClient.getUserById(1L)).thenReturn(userDTO);

        String result = orderService.cancelOrder(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
        verify(notificationClient).sendNotification(eq(2L), anyString());
    }

    @Test
    void hasUserPurchasedProduct_ReturnsTrue() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));

        boolean result = orderService.hasUserPurchasedProduct(1L, 1L);

        assertTrue(result);
    }

    @Test
    void hasUserPurchasedProduct_ReturnsFalse() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));

        boolean result = orderService.hasUserPurchasedProduct(1L, 999L);

        assertFalse(result);
    }
    
    @Test
    void getOrderHistory_Success() {
        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));
        when(productClient.getProductById(1L)).thenReturn(productDTO);
        
        List<OrderHistoryDTO> result = orderService.getOrderHistory(1L);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getOrderId());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals("Test Product", result.get(0).getItems().get(0).getProductName());
    }
}
