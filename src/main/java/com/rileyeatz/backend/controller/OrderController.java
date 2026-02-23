package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.CartItem;
import com.rileyeatz.backend.model.Order;
import com.rileyeatz.backend.model.OrderStatus;
import com.rileyeatz.backend.model.User;
import com.rileyeatz.backend.repository.CartItemRepository;
import com.rileyeatz.backend.repository.OrderRepository;
import com.rileyeatz.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Get logged-in user
        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get cart items
        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        // Calculate total
        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getMenuItem().getPrice() * item.getQuantity())
                .sum();

        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Clear cart
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