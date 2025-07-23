package com.utsavi.oms_backend.service;

import com.utsavi.oms_backend.dto.OrderRequest;
import com.utsavi.oms_backend.dto.OrderResponse;
import com.utsavi.oms_backend.exception.ResourceAlreadyExistsException;
import com.utsavi.oms_backend.model.*;
import com.utsavi.oms_backend.repository.*;
import com.utsavi.oms_backend.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Places a new order after checking for existing records
     *
     * @param orderRequest The order details
     * @return OrderResponse with order details
     * @throws ResourceAlreadyExistsException if a duplicate order is detected
     */
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        AtomicReference<User> userRef = new AtomicReference<>();
        AtomicReference<Address> addressRef = new AtomicReference<>();
        AtomicReference<Product> productRef = new AtomicReference<>();
        AtomicReference<Order> orderRef = new AtomicReference<>();
        AtomicReference<OrderItem> orderItemRef = new AtomicReference<>();

        // Check for existing user by email
        Optional<User> existingUser = userRepository.findByEmail(orderRequest.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            // Update user details if needed
            if (!user.getName().equals(orderRequest.getName()) || !user.getPhoneNo().equals(orderRequest.getPhoneNo())) {
                user.setName(orderRequest.getName());
                user.setPhoneNo(orderRequest.getPhoneNo());
                userRepository.save(user);
                userRef.set(user);
            }
        } else {
            // Create new user
            User user = User.builder()
                    .name(orderRequest.getName())
                    .email(orderRequest.getEmail())
                    .phoneNo(orderRequest.getPhoneNo())
                    .build();
            userRepository.save(user);
            userRef.set(user);
        }

        // Check for existing address
        Optional<Address> existingAddress = addressRepository.findByStreetAddressAndCityAndStateAndZipcode(
                orderRequest.getStreetAddress(),
                orderRequest.getCity(),
                orderRequest.getState(),
                orderRequest.getZipcode()
        );

        if (!existingAddress.isPresent()) {
            Address address = existingAddress.orElseGet(() ->
                    Address.builder()
                            .streetAddress(orderRequest.getStreetAddress())
                            .city(orderRequest.getCity())
                            .state(orderRequest.getState())
                            .zipcode(orderRequest.getZipcode())
                            .build()
            );
            addressRepository.save(address);
            addressRef.set(address);
        } else {
            addressRef.set(existingAddress.get());
        }


        // Check for existing product
        orderRequest.getProducts().stream()
                .map(req -> productRepository.findByName(req.getName())
                        .orElseGet(() -> productRepository.save(Product.builder().name(req.getName()).build()))
                );

        // Create and save the order
        Order order = Order.builder()
                .userId(userRef.get().getUserId())
                .addressId(addressRef.get().getAddressId())
                .orderDate(Timestamp.valueOf(LocalDateTime.now()))
                .status(OrderStatus.Created.toString())
                .specialInstructions(orderRequest.getSpecialInstructions())
                .build();
        order = orderRepository.save(order);
        orderRef.set(order);

        // Check for existing order item
        orderRequest.getProducts().stream()
                .filter(product ->
                        orderItemRepository.findByOrderIdAndProductId(orderRef.get().getOrderId(), productRef.get().getProductId())
                                .isEmpty()
                )
                .map(product -> OrderItem.builder()
                        .orderId(orderRef.get().getOrderId())
                        .productId(productRef.get().getProductId())
                        .quantity(product.getQuantity())
                        .build())
                .forEach(orderItemRepository::save);


        // Build and return response
        return OrderResponse.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .message("Order placed successfully")
                .build();
    }
}
