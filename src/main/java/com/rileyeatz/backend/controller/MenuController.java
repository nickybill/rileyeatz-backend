package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.repository.MenuItemRepository;
import com.rileyeatz.backend.repository.RestaurantRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")   // ✅ allows Android to access backend
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuController(MenuItemRepository menuItemRepository,
                          RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    // ✅ 1️⃣ ADD MENU ITEM (POST)
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

        Optional<Restaurant> restaurant =
                restaurantRepository.findById(restaurantId);

        if (restaurant.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Restaurant not found");
        }

        Double parsedPrice;
        try {
            parsedPrice = Double.parseDouble(price);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid price format");
        }

        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(parsedPrice);
        menuItem.setRestaurant(restaurant.get());

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        return ResponseEntity.ok(savedMenuItem);
    }


    // ✅ 2️⃣ GET MENU ITEMS (NEW - FIXES YOUR 405 ERROR)
    @GetMapping("/{restaurantId}")
    public ResponseEntity<?> getMenuItems(@PathVariable Long restaurantId) {

        Optional<Restaurant> restaurant =
                restaurantRepository.findById(restaurantId);

        if (restaurant.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Restaurant not found");
        }

        List<MenuItem> menuItems =
                menuItemRepository.findByRestaurantId(restaurantId);

        return ResponseEntity.ok(menuItems);
    }
}