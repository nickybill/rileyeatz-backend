package com.rileyeatz.backend.dto;

public class LoginResponse {
    private String token;
    private String restaurantId; // UUID as String

    public LoginResponse(String token, String restaurantId) {
        this.token = token;
        this.restaurantId = restaurantId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRestaurantId() { return restaurantId; }
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
}