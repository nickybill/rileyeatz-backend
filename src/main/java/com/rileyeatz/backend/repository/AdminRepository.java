package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Optional: add custom queries if needed, e.g.,
    Admin findByEmail(String email);
}