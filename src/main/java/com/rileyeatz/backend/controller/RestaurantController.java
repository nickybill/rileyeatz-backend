package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.model.Admin;
import com.rileyeatz.backend.repository.RestaurantRepository;
import com.rileyeatz.backend.repository.AdminRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/restaurants")
@CrossOrigin("*")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private Cloudinary cloudinary;

    // ===== GET ALL =====
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return ResponseEntity.ok(restaurants);
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createRestaurant(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "adminId", required = false) Long adminId
    ) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone(phone);

        // Image upload
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinary.uploader()
                        .upload(image.getBytes(), ObjectUtils.emptyMap())
                        .get("secure_url").toString();
                restaurant.setImageUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Image upload failed: " + e.getMessage());
            }
        }

        // Optional admin linking
        if (adminId != null) {
            Admin admin = adminRepository.findById(adminId).orElse(null);
            if (admin == null) return ResponseEntity.badRequest().body("Admin not found");
            restaurant.setAdmin(admin);
            admin.setRestaurant(restaurant);
        }

        restaurantRepository.save(restaurant);
        return ResponseEntity.ok("Restaurant created successfully");
    }

    // ===== UPDATE =====
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateRestaurant(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "adminId", required = false) Long adminId
    ) {
        Optional<Restaurant> restaurantOptional = restaurantRepository.findById(id);
        if (restaurantOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Restaurant not found");
        }

        Restaurant restaurant = restaurantOptional.get();
        restaurant.setName(name);
        restaurant.setAddress(address);
        restaurant.setPhone(phone);

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudinary.uploader()
                        .upload(image.getBytes(), ObjectUtils.emptyMap())
                        .get("secure_url").toString();
                restaurant.setImageUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Image upload failed: " + e.getMessage());
            }
        }

        if (adminId != null) {
            Admin admin = adminRepository.findById(adminId).orElse(null);
            if (admin == null) return ResponseEntity.badRequest().body("Admin not found");
            restaurant.setAdmin(admin);
            admin.setRestaurant(restaurant);
        }

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