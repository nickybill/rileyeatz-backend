package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.repository.MenuItemRepository;
import com.rileyeatz.backend.repository.RestaurantRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuController(MenuItemRepository menuItemRepository,
                          RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping(
            value = "/{restaurantId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> addMenuItem(
            @PathVariable Long restaurantId,
            @RequestPart("image") MultipartFile image,
            @RequestPart("name") String name,
            @RequestPart("description") String description,
            @RequestPart("price") Double price
    ) {

        Optional<Restaurant> restaurant =
                restaurantRepository.findById(restaurantId);

        if (restaurant.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Restaurant not found");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(price);
        menuItem.setRestaurant(restaurant.get());

        // TODO: Save image path if needed

        menuItemRepository.save(menuItem);

        return ResponseEntity.ok("Menu item added successfully");
    }
}