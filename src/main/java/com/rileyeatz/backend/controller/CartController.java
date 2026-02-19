package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.*;
import com.rileyeatz.backend.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;

    public CartController(CartItemRepository cartItemRepository,
                          UserRepository userRepository,
                          MenuItemRepository menuItemRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.menuItemRepository = menuItemRepository;
    }

    // ➕ Add to Cart (JWT secured)
    @PostMapping("/{menuItemId}")
    public ResponseEntity<?> addToCart(@PathVariable Long menuItemId,
                                       @RequestParam int quantity,
                                       HttpServletRequest request) {

        String email = (String) request.getAttribute("email");

        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(email).orElseThrow();
        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElseThrow();

        Optional<CartItem> existing =
                cartItemRepository.findByUserIdAndMenuItemId(user.getId(), menuItemId);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            return ResponseEntity.ok("Quantity updated");
        }

        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setMenuItem(menuItem);
        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return ResponseEntity.ok("Item added to cart");
    }

    // 📥 Get Cart (JWT secured)
    @GetMapping
    public ResponseEntity<?> getCart(HttpServletRequest request) {

        String email = (String) request.getAttribute("email");

        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userRepository.findByEmail(email).orElseThrow();

        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());

        return ResponseEntity.ok(cartItems);
    }

    // ❌ Remove item
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
        return ResponseEntity.ok("Item removed from cart");
    }

    // 🔄 Update quantity
    @PutMapping("/{cartItemId}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long cartItemId,
                                            @RequestParam int quantity) {

        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);

        if (cartItemOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart item not found");
        }

        CartItem cartItem = cartItemOptional.get();
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return ResponseEntity.ok("Cart updated");
    }
}
