package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.repository.RestaurantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin("*")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    // ===== GET ALL =====
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    // ===== GET BY ID =====
    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurantById(@PathVariable Long id) {

        Optional<Restaurant> restaurant = restaurantRepository.findById(id);

        if (restaurant.isEmpty()) {
            return ResponseEntity.badRequest().body("Restaurant not found");
        }

        return ResponseEntity.ok(restaurant.get());
    }

    // ===== CREATE =====
    @PostMapping
    public ResponseEntity<?> createRestaurant(@RequestBody Restaurant restaurant) {

        restaurantRepository.save(restaurant);
        return ResponseEntity.ok("Restaurant created successfully");
    }

    // ===== UPDATE =====
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRestaurant(@PathVariable Long id,
                                              @RequestBody Restaurant updatedRestaurant) {

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);

        if (restaurantOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Restaurant not found");
        }

        Restaurant restaurant = restaurantOptional.get();
        restaurant.setName(updatedRestaurant.getName());
        restaurant.setAddress(updatedRestaurant.getAddress());
        restaurant.setPhone(updatedRestaurant.getPhone());
        restaurant.setImageUrl(updatedRestaurant.getImageUrl());

        restaurantRepository.save(restaurant);

        return ResponseEntity.ok("Restaurant updated successfully");
    }

    // ===== DELETE =====
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRestaurant(@PathVariable Long id) {

        if (!restaurantRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Restaurant not found");
        }

        restaurantRepository.deleteById(id);

        return ResponseEntity.ok("Restaurant deleted successfully");
    }
}
