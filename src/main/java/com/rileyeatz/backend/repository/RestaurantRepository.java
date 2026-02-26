package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    Optional<Restaurant> findById(UUID id);

    void deleteById(UUID id);
}