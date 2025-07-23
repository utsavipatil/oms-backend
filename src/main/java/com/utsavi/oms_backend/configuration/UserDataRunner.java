//package com.utsavi.oms_backend.configuration;
//
//import com.utsavi.oms_backend.model.Address;
//import com.utsavi.oms_backend.model.Order;
//import com.utsavi.oms_backend.model.OrderItem;
//import com.utsavi.oms_backend.model.Product;
//import com.utsavi.oms_backend.model.User;
//import com.utsavi.oms_backend.repository.AddressRepository;
//import com.utsavi.oms_backend.repository.OrderItemRepository;
//import com.utsavi.oms_backend.repository.OrderRepository;
//import com.utsavi.oms_backend.repository.ProductRepository;
//import com.utsavi.oms_backend.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.sql.Timestamp;
//
///* Test User Data Runner */
//@Configuration  // This marks the class as a source of beans
//public class UserDataRunner {
//
////    @Bean
////    public CommandLineRunner runUserTest(UserRepository userRepository) {
////        return args -> {
////            // Create a new user
////            User user = new User(null, "Utsavi Patil", "utsavi.patil@example.com", "9999999999");
////            userRepository.save(user);
////
////            // Fetch and print all users
////            userRepository.findAll().forEach(System.out::println);
////        };
////    }
//
////        @Bean
////    public CommandLineRunner runProductTest(ProductRepository productRepository) {
////        return args -> {
////            // Create a new user
////            Product product = new Product(null, "Laptop");
////            productRepository.save(product);
////
////            // Fetch and print all users
////            productRepository.findAll().forEach(System.out::println);
////        };
////    }
//
//    @Bean
//    public CommandLineRunner runTest(UserRepository userRepository,
//                                   ProductRepository productRepository,
//                                   OrderRepository orderRepository,
//                                   AddressRepository addressRepository,
//                                   OrderItemRepository orderItemRepository) {
//        return args -> {
//            // Check if test user already exists
//            User user = userRepository.findByEmail("test@example.com")
//                .orElseGet(() -> {
//                    // Create and save a new user if not exists
//                    User newUser = new User(null, "Test User", "test@example.com", "1234567890");
//                    return userRepository.save(newUser);
//                });
//            System.out.println("Using user: " + user.getUserId() + " - " + user.getName());
//
//            // Check if test product already exists
//            Product product = productRepository.findByName("Test Product")
//                .stream()
//                .findFirst()
//                .orElseGet(() -> {
//                    // Create and save a new product if not exists
//                    Product newProduct = new Product(null, "Test Product");
//                    return productRepository.save(newProduct);
//                });
//            System.out.println("Using product: " + product.getProductId() + " - " + product.getName());
//
//            // Check if test address already exists
//            Address address = addressRepository.findByStreetAddress("123 Test St")
//                .stream()
//                .findFirst()
//                .orElseGet(() -> {
//                    // Create and save a new address if not exists
//                    Address newAddress = new Address(
//                        null,
//                        "123 Test St",
//                        "Test City",
//                        "Test State",
//                        "12345",
//                        "Test instructions"
//                    );
//                    return addressRepository.save(newAddress);
//                });
//            System.out.println("Using address: " + address.getAddressId() + " - " + address.getStreetAddress());
//
//            // Create or get an order
//            Order order;
//            if (orderRepository.count() == 0) {
//                // Create a new order if none exists
//                order = new Order(
//                    null,
//                    user.getUserId(),
//                    address.getAddressId(),
//                    new Timestamp(System.currentTimeMillis()),
//                    "Pending"
//                );
//                order = orderRepository.save(order);
//                System.out.println("Created new order: " + order.getOrderId());
//            } else {
//                // Use the first existing order
//                order = orderRepository.findAll().get(0);
//                System.out.println("Using existing order: " + order.getOrderId());
//            }
//
//            // Test OrderItems
//            System.out.println("\n=== Testing OrderItems ===");
//
//            // Clear existing order items to avoid duplicates
//            orderItemRepository.deleteAll();
//
//            // Create test order item
//            OrderItem orderItem = new OrderItem(
//                null,
//                order.getOrderId(),
//                product.getProductId(),
//                2  // quantity
//            );
//            orderItem = orderItemRepository.save(orderItem);
//            System.out.println("Created order item: " + orderItem.getOrderItemId() +
//                " for order " + orderItem.getOrderId() +
//                " with product " + orderItem.getProductId() +
//                ", quantity: " + orderItem.getQuantity());
//
//            // Find and print all order items
//            System.out.println("\nAll order items:");
//            orderItemRepository.findAll().forEach(item ->
//                System.out.println("OrderItem ID: " + item.getOrderItemId() +
//                    ", Order ID: " + item.getOrderId() +
//                    ", Product ID: " + item.getProductId() +
//                    ", Quantity: " + item.getQuantity())
//            );
//
//            // Find order items by order ID
//            System.out.println("\nOrder items for order " + order.getOrderId() + ":");
//            orderItemRepository.findByOrderId(order.getOrderId()).forEach(item ->
//                System.out.println("OrderItem ID: " + item.getOrderItemId() +
//                    ", Product ID: " + item.getProductId() +
//                    ", Quantity: " + item.getQuantity())
//            );
//
//            // Print all data
//            System.out.println("\n=== All Users ===");
//            userRepository.findAll().forEach(u -> System.out.println(
//                "User ID: " + u.getUserId() + " | Name: " + u.getName() + " | Email: " + u.getEmail()));
//
//            System.out.println("\n=== All Products ===");
//            productRepository.findAll().forEach(p -> System.out.println(
//                "Product ID: " + p.getProductId() + " | Name: " + p.getName()));
//
//            System.out.println("\n=== All Addresses ===");
//            addressRepository.findAll().forEach(a -> System.out.println(
//                "Address ID: " + a.getAddressId() + " | " + a.getStreetAddress() + ", " + a.getCity() + ", " + a.getState() + " " + a.getZipcode()));
//
//            System.out.println("\n=== All Orders ===");
//            orderRepository.findAll().forEach(o -> System.out.println(
//                "Order ID: " + o.getOrderId() +
//                " | User ID: " + o.getUserId() +
//                " | Address ID: " + o.getAddressId() +
//                " | Date: " + o.getOrderDate() +
//                " | Status: " + o.getStatus()));
//        };
//    }
//
//}
//
