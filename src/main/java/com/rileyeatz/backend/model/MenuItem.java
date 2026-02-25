package com.rileyeatz.backend.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Double price;

    private String imageUrl;   // ✅ ADDED FIELD

    @ManyToOne
    @JsonIgnore   // prevents infinite JSON loop
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    // ===== GETTERS =====

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {   // ✅ ADDED GETTER
        return imageUrl;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    // ===== SETTERS =====

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {   // ✅ ADDED SETTER
        this.imageUrl = imageUrl;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}