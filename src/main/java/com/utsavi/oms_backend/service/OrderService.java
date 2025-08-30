package com.utsavi.oms_backend.service;

import com.utsavi.oms_backend.dto.OrderCreatedEvent;
import com.utsavi.oms_backend.dto.OrderRequest;
import com.utsavi.oms_backend.dto.OrderResponse;
import com.utsavi.oms_backend.exception.ResourceAlreadyExistsException;
import com.utsavi.oms_backend.model.*;
import com.utsavi.oms_backend.producer.OrderEventProducer;
import com.utsavi.oms_backend.repository.*;
import com.utsavi.oms_backend.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@Transactional
public class OrderService {

    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderEventProducer orderEventProducer;

    @Autowired
    public OrderService(AddressRepository addressRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository, ProductRepository productRepository,
                        UserRepository userRepository, OrderEventProducer orderEventProducer) {
        this.addressRepository = addressRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderEventProducer = orderEventProducer;
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
                .specialInstructions(order.getSpecialInstructions()).build();
    }
}
