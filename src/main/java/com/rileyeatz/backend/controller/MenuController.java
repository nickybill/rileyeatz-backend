package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.MenuItem;
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
    private final Cloudinary cloudinary;

    public MenuController(MenuItemRepository menuItemRepository,
                          RestaurantRepository restaurantRepository,
                          Cloudinary cloudinary) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.cloudinary = cloudinary;
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
            // Upload image to Cloudinary
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

            menuItem.setImageUrl(imageUrl);

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

    // ✅ 3️⃣ DELETE MENU ITEM
    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long menuItemId) {

        Optional<MenuItem> menuItem = menuItemRepository.findById(menuItemId);

        if (menuItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Menu item not found");
        }

        menuItemRepository.deleteById(menuItemId);

        return ResponseEntity.ok("Menu item deleted successfully");
    }

    // ✅ 4️⃣ UPDATE MENU ITEM (PUT)
    @PutMapping(value = "/{menuItemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMenuItem(
            @PathVariable Long menuItemId,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("price") String price
    ) {

        Optional<MenuItem> optionalItem = menuItemRepository.findById(menuItemId);

        if (optionalItem.isEmpty()) {
            return ResponseEntity.badRequest().body("Menu item not found");
        }

        MenuItem menuItem = optionalItem.get();
        menuItem.setName(name);
        menuItem.setDescription(description);
        menuItem.setPrice(Double.parseDouble(price));

        // If new image uploaded → update Cloudinary
        if (image != null && !image.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(
                        image.getBytes(),
                        ObjectUtils.emptyMap()
                );
                String imageUrl = uploadResult.get("secure_url").toString();
                menuItem.setImageUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Image upload failed");
            }
        }

        menuItemRepository.save(menuItem);

        return ResponseEntity.ok(menuItem);
    }
}