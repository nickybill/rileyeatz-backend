package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.*;
import com.rileyeatz.backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository,
                           CartItemRepository cartItemRepository,
                           UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    // ✅ GET ALL ORDERS (Admin or General Use)
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // ✅ CHECKOUT
    @PostMapping("/checkout")
    public Order checkout(Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        double total = 0;

        Order order = new Order();
        order.setUser(user);

        for (CartItem cartItem : cartItems) {

            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(cartItem.getMenuItem());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);

            total += cartItem.getMenuItem().getPrice() * cartItem.getQuantity();
        }

        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartItemRepository.deleteAll(cartItems);

        return savedOrder;
    }

    // ✅ GET MY ORDERS
    @GetMapping("/my")
    public List<Order> getMyOrders(Authentication authentication) {

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return orderRepository.findByUserId(user.getId());
    }

    // ✅ UPDATE ORDER STATUS (Admin Use)
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable Long orderId,
                                   @RequestParam OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(status);
        return orderRepository.save(order);
    }

    // ✅ GET ORDERS BY STATUS
    @GetMapping("/status")
    public List<Order> getOrdersByStatus(@RequestParam OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
}