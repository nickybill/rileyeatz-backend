package com.rileyeatz.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    // Getters & Setters

    public Long getId() { return id; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public Order getOrder() { return order; }

    public void setOrder(Order order) { this.order = order; }

    public MenuItem getMenuItem() { return menuItem; }

    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
}
