package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.CartItem;
import com.rileyeatz.backend.model.MenuItem;
import com.rileyeatz.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional<CartItem> findByUserAndMenuItem(User user, MenuItem menuItem);
}