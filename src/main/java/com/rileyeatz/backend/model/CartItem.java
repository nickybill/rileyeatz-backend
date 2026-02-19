package com.rileyeatz.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "menu_item_id")
    private MenuItem menuItem;

    // Getters & Setters

    public Long getId() { return id; }

    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public MenuItem getMenuItem() { return menuItem; }

    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
}
