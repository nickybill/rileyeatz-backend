package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.repository.MenuItemRepository;
import com.rileyeatz.backend.repository.RestaurantRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuController(MenuItemRepository menuItemRepository,
                          RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // Add menu item to restaurant
    @PostMapping("/{restaurantId}")
    public ResponseEntity<?> addMenuItem(@PathVariable Long restaurantId,
                                         @RequestBody MenuItem menuItem) {

        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(restaurantId);

        if (restaurantOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Restaurant not found");
        }

        menuItem.setRestaurant(restaurantOptional.get());
        menuItemRepository.save(menuItem);

        return ResponseEntity.ok("Menu item added successfully");
    }

    // Get menu by restaurant
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getMenuByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuItemRepository.findByRestaurantId(restaurantId));
    }

    // Delete menu item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id) {
        menuItemRepository.deleteById(id);
        return ResponseEntity.ok("Menu item deleted");
    }
}
