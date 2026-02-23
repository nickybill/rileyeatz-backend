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
    public ResponseEntity<?> checkout() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        // 🔒 Prevent crash
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated. Please login first.");
        }

        String username = authentication.getName();

        return ResponseEntity.ok("Checkout successful for user: " + username);
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