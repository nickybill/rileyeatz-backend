package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.User;
import com.rileyeatz.backend.service.CartService;
import com.rileyeatz.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService,
                          UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/{menuItemId}")
    public ResponseEntity<?> addToCart(
            @PathVariable Long menuItemId,
            @RequestParam int quantity,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        cartService.addToCart(user, menuItemId, quantity);

        return ResponseEntity.ok("Item added to cart");
    }
}