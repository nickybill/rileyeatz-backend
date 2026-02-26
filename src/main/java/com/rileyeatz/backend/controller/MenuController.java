package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.Admin;
import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.Restaurant;
import com.rileyeatz.backend.repository.AdminRepository;
import com.rileyeatz.backend.repository.MenuItemRepository;
import com.rileyeatz.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin("*")
public class MenuController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<?> addMenuItem(@RequestHeader("Authorization") String token,
                                         @RequestBody MenuItem item) {
        String adminEmail = jwtUtil.extractUsername(token.replace("Bearer ", ""));
        Admin admin = adminRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Restaurant restaurant = admin.getRestaurant();
        if (restaurant == null) return ResponseEntity.badRequest().body("Restaurant not found");

        item.setRestaurant(restaurant);
        menuItemRepository.save(item);

        return ResponseEntity.ok("Item added successfully");
    }
}