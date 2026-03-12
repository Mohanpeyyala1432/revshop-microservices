package com.revshop.orderservice.service;

import com.revshop.orderservice.dto.*;
import com.revshop.orderservice.model.*;
import com.revshop.orderservice.repository.*;
import com.revshop.orderservice.client.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private CartClient cartClient;
    
    @Autowired
    private ProductClient productClient;
    
    @Autowired
    private NotificationClient notificationClient;
    
    @Autowired
    private PaymentClient paymentClient;
    
    @Autowired
    private UserClient userClient;
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<SellerOrderViewDTO> getOrdersForSeller(Long sellerId) {
        List<Order> allOrders = orderRepository.findAll();
        List<SellerOrderViewDTO> sellerOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                continue;
            }
            
            for (OrderItem item : order.getOrderItems()) {
                try {
                    ProductDTO product = productClient.getProductById(item.getProductId());
                    
                    if (product.getSellerId() != null && product.getSellerId().equals(sellerId)) {
                        SellerOrderViewDTO dto = new SellerOrderViewDTO();
                        dto.setOrderId(order.getOrderId());
                        
                        // Fetch buyer details
                        try {
                            UserDTO buyer = userClient.getUserById(order.getUserId());
                            dto.setBuyerName(buyer.getName());
                            dto.setBuyerEmail(buyer.getEmail());
                            dto.setBuyerPhone(buyer.getPhone() != null ? buyer.getPhone() : "N/A");
                        } catch (Exception e) {
                            System.err.println("Failed to fetch buyer details for userId: " + order.getUserId() + ", Error: " + e.getMessage());
                            e.printStackTrace();
                            dto.setBuyerName("Buyer");
                            dto.setBuyerEmail("buyer@email.com");
                            dto.setBuyerPhone("N/A");
                        }
                        
                        dto.setProductName(product.getProductName());
                        dto.setQuantity(item.getQuantity());
                        dto.setPrice(item.getPrice());
                        dto.setTotalAmount(item.getQuantity() * item.getPrice());
                        dto.setStatus(order.getStatus().name());
                        sellerOrders.add(dto);
                    }
                } catch (Exception e) {
                    // Skip products that can't be fetched (deleted/unavailable)
                    continue;
                }
            }
        }
        
        return sellerOrders;
    }

    public void updateOrderStatusBySeller(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot update status of a cancelled order");
        }
        
        order.setStatus(status);
        orderRepository.save(order);
    }

    @CircuitBreaker(name = "cartService", fallbackMethod = "checkoutFallback")
    public CheckoutResponseDTO checkout(Long userId, CheckoutRequestDTO request) {
        CartResponseDTO cart = cartClient.getCart(userId);
        double total = cart.getItems().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        
        Long sellerId = cart.getItems().isEmpty() ? null : 
            productClient.getProductById(cart.getItems().get(0).getProductId()).getSellerId();
        
        Order order = new Order();
        order.setUserId(userId);
        order.setSellerId(sellerId);
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(total);
        
        order.setStatus(OrderStatus.CONFIRMED);
        
        List<OrderItem> items = cart.getItems().stream()
            .map(cartItem -> {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProductId(cartItem.getProductId());
                item.setQuantity(cartItem.getQuantity());
                item.setPrice(cartItem.getPrice());
                return item;
            })
            .collect(Collectors.toList());
        order.setOrderItems(items);
        
        Order saved = orderRepository.save(order);
        System.out.println(">>> Order saved with ID: " + saved.getOrderId());
        
        // Clear cart after successful order
        System.out.println(">>> About to clear cart for user: " + userId);
        cartClient.clearCart(userId);
        System.out.println(">>> Cart cleared successfully for user: " + userId);
        
        // Send buyer notification
        try {
            notificationClient.sendNotification(userId, 
                "Your order #" + saved.getOrderId() + " has been placed successfully! Total: ₹" + saved.getTotalAmount());
        } catch (Exception e) {
        }
        
        // Send seller notification
        if (sellerId != null) {
            try {
                notificationClient.sendNotification(sellerId, 
                    "New order #" + saved.getOrderId() + " received! Total: ₹" + saved.getTotalAmount());
            } catch (Exception e) {
            }
        }
        
        return new CheckoutResponseDTO(saved.getOrderId(), saved.getStatus().name(), saved.getTotalAmount());
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "buyNowFallback")
    public CheckoutResponseDTO buyNow(Long userId, BuyNowRequestDTO request) {
        ProductDTO product = productClient.getProductById(request.getProductId());
        double total = product.getPrice() * request.getQuantity();
        
        Order order = new Order();
        order.setUserId(userId);
        order.setSellerId(product.getSellerId());
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.CONFIRMED);
        
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(request.getProductId());
        item.setQuantity(request.getQuantity());
        item.setPrice(product.getPrice());
        order.setOrderItems(List.of(item));
        
        Order saved = orderRepository.save(order);
        
        // Send buyer notification
        try {
            notificationClient.sendNotification(userId, 
                "Your order #" + saved.getOrderId() + " has been placed successfully! Total: ₹" + saved.getTotalAmount());
        } catch (Exception e) {
        }
        
        // Send seller notification
        if (product.getSellerId() != null) {
            try {
                notificationClient.sendNotification(product.getSellerId(), 
                    "New order #" + saved.getOrderId() + " received! Total: ₹" + saved.getTotalAmount());
            } catch (Exception e) {
            }
        }
        
        return new CheckoutResponseDTO(saved.getOrderId(), saved.getStatus().name(), saved.getTotalAmount());
    }

    public List<OrderHistoryDTO> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
            .map(order -> {
                List<OrderItemDTO> items = order.getOrderItems().stream()
                    .map(item -> {
                        try {
                            ProductDTO product = productClient.getProductById(item.getProductId());
                            return new OrderItemDTO(
                                item.getProductId(),
                                product.getProductName(),
                                item.getPrice(),
                                item.getQuantity()
                            );
                        } catch (Exception e) {
                            return new OrderItemDTO(
                                item.getProductId(),
                                "Product Unavailable",
                                item.getPrice(),
                                item.getQuantity()
                            );
                        }
                    })
                    .collect(Collectors.toList());
                
                OrderHistoryDTO dto = new OrderHistoryDTO();
                dto.setOrderId(order.getOrderId());
                dto.setStatus(order.getStatus().name());
                dto.setTotalAmount(order.getTotalAmount());
                dto.setShippingAddress(order.getShippingAddress());
                dto.setBillingAddress(order.getBillingAddress());
                dto.setItems(items);
                
                // Fetch payment details
                try {
                    var payment = paymentClient.getPaymentByOrderId(order.getOrderId());
                    if (payment != null) {
                        dto.setPaymentMethod(payment.getType());
                        dto.setTransactionId(payment.getTransactionId());
                        dto.setPaymentDate(payment.getPaymentDate());
                    }
                } catch (Exception e) {
                    // Payment details not available
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }

    public String cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        // Check payment method
        String message = "Order cancelled successfully";
        try {
            var payment = paymentClient.getPaymentByOrderId(orderId);
            if (payment != null && !"COD".equals(payment.getType())) {
                message = "Order cancelled successfully. Your amount will be refunded within 5-7 business days";
            }
        } catch (Exception e) {
            // If payment fetch fails, use default message
        }
        
        // Send seller notification with buyer name
        if (order.getSellerId() != null) {
            try {
                String buyerName = "Buyer";
                try {
                    UserDTO buyer = userClient.getUserById(order.getUserId());
                    buyerName = buyer.getName();
                } catch (Exception e) {
                    // Use default if fetch fails
                }
                notificationClient.sendNotification(order.getSellerId(), 
                    "Order #" + orderId + " has been cancelled by " + buyerName);
            } catch (Exception e) {
                // Log but don't fail the cancellation
            }
        }
        
        return message;
    }
    
    public boolean hasUserPurchasedProduct(Long userId, Long productId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
            .filter(order -> order.getStatus() != OrderStatus.CANCELLED)
            .flatMap(order -> order.getOrderItems().stream())
            .anyMatch(item -> item.getProductId().equals(productId));
    }
    
    private CheckoutResponseDTO checkoutFallback(Long userId, CheckoutRequestDTO request, Exception e) {
        throw new RuntimeException("Cart service is currently unavailable. Please try again later.");
    }
    
    private CheckoutResponseDTO buyNowFallback(Long userId, BuyNowRequestDTO request, Exception e) {
        throw new RuntimeException("Product service is currently unavailable. Please try again later.");
    }
}
