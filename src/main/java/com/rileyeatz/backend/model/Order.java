package com.rileyeatz.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double totalAmount;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Many Orders → One User
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // One Order → Many OrderItems
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING; // default status
    }

    public Order(Double totalAmount, User user, List<OrderItem> orderItems) {
        this.totalAmount = totalAmount;
        this.user = user;
        this.orderItems = orderItems;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
