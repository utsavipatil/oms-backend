package com.utsavi.oms_backend.service;

import com.utsavi.oms_backend.dto.*;
import com.utsavi.oms_backend.dto.orderTable.Item;
import com.utsavi.oms_backend.dto.orderTable.OrderItemDto;
import com.utsavi.oms_backend.exception.ResourceAlreadyExistsException;
import com.utsavi.oms_backend.model.*;
import com.utsavi.oms_backend.producer.OrderEventProducer;
import com.utsavi.oms_backend.producer.OrderStatusProducer;
import com.utsavi.oms_backend.repository.*;
import com.utsavi.oms_backend.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderEventProducer orderEventProducer;
    private final OrderStatusProducer orderStatusProducer;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Autowired
    public OrderService(AddressRepository addressRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository,
                        UserRepository userRepository, OrderEventProducer orderEventProducer, OrderStatusHistoryRepository orderStatusHistoryRepository,
                        OrderStatusProducer orderStatusProducer) {
        this.addressRepository = addressRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderEventProducer = orderEventProducer;
        this.orderStatusProducer = orderStatusProducer;
    }

    /**
     * Places a new order after checking for existing records
     *
     * @param orderRequest The order details
     * @return OrderResponse with order details
     * @throws ResourceAlreadyExistsException if a duplicate order is detected
     */
    public OrderResponse placeOrder(OrderRequest orderRequest) {

        //Save order to db
        OrderCreatedEvent orderCreatedEvent = saveOrder(orderRequest);

        //Map and send to Kafka
        orderEventProducer.publishOrderCreatedEvent(orderCreatedEvent);

        return OrderResponse.builder()
                .orderId(orderCreatedEvent.getOrderId())
                .status(orderCreatedEvent.getStatus())
                .message("Order placed successfully")
                .build();
    }

    public OrderCreatedEvent saveOrder(OrderRequest orderRequest) {
        // Step 1: Find or create the user
        User user = userRepository.findByEmail(orderRequest.getEmail())
                .map(existingUser -> {
                    existingUser.setName(orderRequest.getName());
                    existingUser.setPhoneNo(orderRequest.getPhoneNo());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .name(orderRequest.getName())
                            .email(orderRequest.getEmail())
                            .phoneNo(orderRequest.getPhoneNo())
                            .build();
                    return userRepository.save(newUser);
                });

        // Step 2: Find or create the address
        Address address = addressRepository.findByStreetAddressAndCityAndStateAndZipcode(
                orderRequest.getStreetAddress(),
                orderRequest.getCity(),
                orderRequest.getState(),
                orderRequest.getZipcode()
        ).orElseGet(() -> {
            Address newAddress = Address.builder()
                    .streetAddress(orderRequest.getStreetAddress())
                    .city(orderRequest.getCity())
                    .state(orderRequest.getState())
                    .zipcode(orderRequest.getZipcode())
                    .build();
            return addressRepository.save(newAddress);
        });

        // Step 3: Create and save the order
        Order order = Order.builder()
                .userId(user.getUserId())
                .addressId(address.getAddressId())
                .orderDate(Timestamp.valueOf(LocalDateTime.now()))
                .status(OrderStatus.Created.toString())
                .totalAmount(orderRequest.getTotalAmount())
                .specialInstructions(orderRequest.getSpecialInstructions())
                .build();
        order = orderRepository.save(order);

        // Step 4: Process order items
        Order finalOrder = order;
        orderRequest.getProducts().forEach(productRequest -> {
            Product product = productRepository.findByName(productRequest.getName())
                    .orElseGet(() -> {
                        Product newProduct = Product.builder().name(productRequest.getName()).build();
                        return productRepository.save(newProduct);
                    });

            OrderItem orderItem = OrderItem.builder()
                    .orderId(finalOrder.getOrderId())
                    .productId(product.getProductId())
                    .quantity(productRequest.getQuantity())
                    .price(productRequest.getPrice())
                    .build();
            orderItemRepository.save(orderItem);
        });

        // Step 5: Build and return response
        return OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .products(orderRequest.getProducts())
                .streetAddress(address.getStreetAddress())
                .city(address.getCity())
                .state(address.getState())
                .zipcode(address.getZipcode())
                .specialInstructions(order.getSpecialInstructions())
                .status(order.getStatus()).build();
    }

    public OrderStatusHistory updateOrderStatus(Integer orderId, String newOrderStatus, String changeBy) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        String prevStatus = order.getStatus();
        //TODO: Add Validation for Status Transition

        order.setStatus(newOrderStatus); //update status
        orderRepository.save(order);

        //Save to Order History table
        OrderStatusHistory history = OrderStatusHistory.builder()
                .orderId(orderId)
                .prevStatus(prevStatus)
                .newStatus(newOrderStatus)
                .changedAt(LocalDateTime.now())
                .changeBy(changeBy).build();

        orderStatusHistoryRepository.save(history);

        //Publish to Kafka event
        orderStatusProducer.publishStatusChange(history);

        return history;
    }

    public List<OrderStatusHistory> getOrderStatusHistory(Integer orderId) {
        return orderStatusHistoryRepository.findByOrderIdOrderByChangedAtAsc(orderId);
    }

    public OrderStatusResponse getCurrentOrderStatus(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return OrderStatusResponse.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .changedAt(order.getOrderDate()).build();
    }

    public List<OrderItemDto> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findAll(pageable);

        List<OrderItemDto> orderItemDtoList = new ArrayList<>();

        for (Order order : orderPage) {
            User user = userRepository.findById(order.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found for order: " + order.getOrderId()));
            Address address = addressRepository.findById(order.getAddressId()).orElseThrow(() -> new IllegalArgumentException("Address not found for order: " + order.getAddressId()));
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getOrderId());
            List<Item> items = orderItems.stream()
                    .map(orderItem -> {
                        Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> new IllegalArgumentException("Product not found for order: " + orderItem.getProductId()));
                        return Item.builder()
                                .name(product.getName())
                                .quantity(orderItem.getQuantity())
                                .build();
                    })
                    .collect(Collectors.toList());

            OrderItemDto orderItemDto = OrderItemDto.builder()
                    .order(order)
                    .user(user)
                    .address(address)
                    .itemList(items).build();
            orderItemDtoList.add(orderItemDto);
        }

        return orderItemDtoList;
    }
}
