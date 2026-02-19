package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (used for login & register validation)
    Optional<User> findByEmail(String email);

    // Optional: check if email exists (alternative method)
    boolean existsByEmail(String email);
}
