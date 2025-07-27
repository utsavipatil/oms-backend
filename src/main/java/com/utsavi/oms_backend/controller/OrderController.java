package com.utsavi.oms_backend.controller;

import com.utsavi.oms_backend.dto.OrderRequest;
import com.utsavi.oms_backend.dto.OrderResponse;
import com.utsavi.oms_backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling order-related HTTP requests.
 * This controller provides endpoints for order operations such as placing new orders.
 * All endpoints are prefixed with "/order".
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Handles HTTP POST requests for placing a new order.
     *
     * @param orderRequest The order details including customer information, shipping address and order items
     * @return ResponseEntity with HTTP 200 OK status if the order is placed successfully
     */
    @PostMapping("/place-order")
    public ResponseEntity<OrderResponse> placeOrder(@Validated @RequestBody OrderRequest orderRequest) {
        log.info("Received order request from customer: {}", orderRequest.getEmail());
        OrderResponse orderResponse = orderService.placeOrder(orderRequest);
        return ResponseEntity.ok(orderResponse);
    }
}
