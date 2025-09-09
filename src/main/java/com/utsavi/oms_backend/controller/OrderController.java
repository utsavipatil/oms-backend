package com.utsavi.oms_backend.controller;

import com.utsavi.oms_backend.dto.OrderRequest;
import com.utsavi.oms_backend.dto.OrderResponse;
import com.utsavi.oms_backend.dto.OrderStatusRequest;
import com.utsavi.oms_backend.dto.OrderStatusResponse;
import com.utsavi.oms_backend.dto.orderTable.OrderItemDto;
import com.utsavi.oms_backend.model.OrderStatusHistory;
import com.utsavi.oms_backend.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for handling order-related HTTP requests.
 * This controller provides endpoints for order operations such as placing new orders.
 * All endpoints are prefixed with "/order".
 */
@RestController
@Slf4j
@RequestMapping("/order")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
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

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusHistory> updateStatus(@PathVariable Integer orderId, @Validated @RequestBody OrderStatusRequest orderStatusRequest) {
        OrderStatusHistory orderStatusResponse = orderService.updateOrderStatus(orderId, orderStatusRequest.getNewStatus().toString(), orderStatusRequest.getChangedBy());
        return ResponseEntity.ok(orderStatusResponse);
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderStatusHistory>> getOrderStatusHistory(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderStatusHistory(orderId));
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<OrderStatusResponse> getCurrentOrderStatus(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getCurrentOrderStatus(orderId));
    }

    @GetMapping("/all-orders")
    public ResponseEntity<List<OrderItemDto>> getAllOrders(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(orderService.getAllOrders(page, size));
    }
}
