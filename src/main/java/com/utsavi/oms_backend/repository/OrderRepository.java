package com.utsavi.oms_backend.repository;

import com.utsavi.oms_backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
