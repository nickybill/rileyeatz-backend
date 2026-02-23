package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.CartItem;
import com.rileyeatz.backend.model.Order;
import com.rileyeatz.backend.model.OrderStatus;
import com.rileyeatz.backend.model.User;
import com.rileyeatz.backend.repository.CartItemRepository;
import com.rileyeatz.backend.repository.OrderRepository;
import com.rileyeatz.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;

    public OrderController(UserRepository userRepository,
                           CartItemRepository cartItemRepository,
                           OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
    }

    // ✅ CHECKOUT
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems =
                cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        double total = cartItems.stream()
                .mapToDouble(item ->
                        item.getMenuItem().getPrice()
                                * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return ResponseEntity.ok(savedOrder);
    }

    // ✅ GET USER ORDERS
    @GetMapping
    public ResponseEntity<?> getOrders(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 USE userId VERSION
        return ResponseEntity.ok(
                orderRepository.findByUserId(user.getId())
        );
    }
}