package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.*;
import com.rileyeatz.backend.repository.CartItemRepository;
import com.rileyeatz.backend.repository.OrderRepository;
import com.rileyeatz.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    // ✅ CHECKOUT (Create Order from Cart)
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401)
                    .body("User not authenticated");
        }

        // 1️⃣ Get logged-in user
        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // 2️⃣ Get cart items
        List<CartItem> cartItems =
                cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Cart is empty");
        }

        // 3️⃣ Create new order
        Order order = new Order();
        order.setUser(user);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new java.util.ArrayList<>();

        for (CartItem cartItem : cartItems) {

            OrderItem orderItem = new OrderItem();

            orderItem.setMenuItem(cartItem.getMenuItem()); // ✅ FIXED
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getMenuItem().getPrice());
            orderItem.setOrder(order); // VERY IMPORTANT

            totalAmount += cartItem.getMenuItem().getPrice()
                    * cartItem.getQuantity();

            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems);

        // 4️⃣ Save order (Cascade saves orderItems)
        Order savedOrder = orderRepository.save(order);

        // 5️⃣ Clear cart
        cartItemRepository.deleteAll(cartItems);

        return ResponseEntity.ok(savedOrder);
    }

    // ✅ GET ALL ORDERS FOR LOGGED-IN USER
    @GetMapping
    public ResponseEntity<?> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUser(user);

        return ResponseEntity.ok(orders);
    }

    // ✅ UPDATE ORDER STATUS (Admin use)
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);

        return ResponseEntity.ok(orderRepository.save(order));
    }
}