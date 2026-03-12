

package com.revshop.orderservice.repository;

import com.revshop.orderservice.model.OrderItem;
import com.revshop.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
}


