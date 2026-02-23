package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.CartItem;
import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.User;
import com.rileyeatz.backend.repository.CartItemRepository;
import com.rileyeatz.backend.repository.MenuItemRepository;
import com.rileyeatz.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;

    public CartController(CartItemRepository cartItemRepository,
                          MenuItemRepository menuItemRepository,
                          UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
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

        Optional<MenuItem> menuItemOptional =
                menuItemRepository.findById(menuItemId);

        if (menuItemOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Menu item not found");
        }

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);  // ✅ matches your repo
        cartItem.setMenuItem(menuItemOptional.get());
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return ResponseEntity.ok("Item added to cart");
    }
}