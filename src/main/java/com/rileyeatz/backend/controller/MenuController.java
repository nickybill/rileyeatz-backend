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
            @RequestParam("image") MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price
    ) {

        // ✅ Check if restaurant exists
        Optional<Restaurant> restaurant =
                restaurantRepository.findById(restaurantId);

        if (restaurant.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Restaurant not found");
        }

        // ✅ Parse price safely (CHANGED - added try/catch)
        Double parsedPrice;
        try {
            parsedPrice = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid price format");
        }

        // ✅ Create menu item
        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(parsedPrice);
        menuItem.setRestaurant(restaurant.get());

        // ✅ CHANGED: Store returned saved object
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        // ✅ CHANGED: Return JSON object instead of String
        return ResponseEntity.ok(savedMenuItem);
    }
}