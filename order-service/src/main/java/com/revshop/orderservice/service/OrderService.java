package com.revshop.orderservice.service;

import com.revshop.orderservice.dto.*;
import com.revshop.orderservice.model.Order;
import com.revshop.orderservice.model.OrderStatus;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> getOrdersByUserId(Long userId);
    List<SellerOrderViewDTO> getOrdersForSeller(Long sellerId);
    void updateOrderStatusBySeller(Long orderId, OrderStatus status);
    CheckoutResponseDTO checkout(Long userId, CheckoutRequestDTO request);
    CheckoutResponseDTO buyNow(Long userId, BuyNowRequestDTO request);
    List<OrderHistoryDTO> getOrderHistory(Long userId);
    String cancelOrder(Long orderId);
    boolean hasUserPurchasedProduct(Long userId, Long productId);
}
