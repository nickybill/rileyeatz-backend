package com.rileyeatz.backend.repository;

import com.rileyeatz.backend.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> { }