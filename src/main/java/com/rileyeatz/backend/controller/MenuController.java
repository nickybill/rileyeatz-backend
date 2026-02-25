package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.repository.MenuItemRepository;
import com.rileyeatz.backend.repository.RestaurantRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final Cloudinary cloudinary;   // ✅ added

    public MenuController(MenuItemRepository menuItemRepository,
                          RestaurantRepository restaurantRepository,
                          Cloudinary cloudinary) {   // ✅ added
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.cloudinary = cloudinary;   // ✅ added
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

        try {

            // ✅ YOUR EXACT CLOUDINARY CODE ADDED HERE
            Map uploadResult = cloudinary.uploader().upload(
                    image.getBytes(),
                    ObjectUtils.emptyMap()
            );

            String imageUrl = uploadResult.get("secure_url").toString();

            MenuItem menuItem = new MenuItem();
            menuItem.setName(name);
            menuItem.setDescription(description);
            menuItem.setPrice(parsedPrice);
            menuItem.setRestaurant(restaurant.get());

            menuItem.setImageUrl(imageUrl);   // ✅ saves Cloudinary URL

            MenuItem savedMenuItem = menuItemRepository.save(menuItem);

            return ResponseEntity.ok(savedMenuItem);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Image upload failed");
        }
    }

    // ✅ 2️⃣ GET MENU ITEMS
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