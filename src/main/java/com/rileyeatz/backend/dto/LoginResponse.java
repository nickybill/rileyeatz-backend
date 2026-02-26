package com.rileyeatz.backend.dto;

public class LoginResponse {

    private String token;
    private Long restaurantId; // optional for admin

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, Long restaurantId) {
        this.token = token;
        this.restaurantId = restaurantId;
    }

    public String getToken() { return token; }
    public Long getRestaurantId() { return restaurantId; }
}