package com.utsavi.oms_backend.repository;

import com.utsavi.oms_backend.model.Order;
import com.utsavi.oms_backend.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    Optional<OrderItem> findByOrderIdAndProductId(Integer orderId, Integer productId);
}
