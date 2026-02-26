package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Fetch admin by email with restaurant eagerly loaded
    @Query("SELECT a FROM Admin a LEFT JOIN FETCH a.restaurant WHERE a.email = :email")
    Optional<Admin> findByEmailWithRestaurant(@Param("email") String email);
}