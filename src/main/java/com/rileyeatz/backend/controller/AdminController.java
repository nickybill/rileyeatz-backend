package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.Admin;
import com.rileyeatz.backend.payload.PasswordChangeRequest;
import com.rileyeatz.backend.repository.AdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    // ===== Dashboard (existing) =====
    @GetMapping("/dashboard")
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pending", 12);
        stats.put("preparing", 5);
        stats.put("delivered", 30);
        stats.put("cancelled", 2);
        return stats;
    }

    // ===== Get Admin Profile =====
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        // For simplicity, fetch the first admin
        Admin admin = adminRepository.findById(1L).orElse(null);
        if (admin == null) {
            return ResponseEntity.badRequest().body("Admin not found");
        }
        return ResponseEntity.ok(admin);
    }

    // ===== Update Admin Profile =====
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody Admin updatedAdmin) {

        Admin admin = adminRepository.findById(1L).orElse(null);
        if (admin == null) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        admin.setName(updatedAdmin.getName());
        admin.setEmail(updatedAdmin.getEmail());
        admin.setPhone(updatedAdmin.getPhone());

        adminRepository.save(admin);

        return ResponseEntity.ok(admin);
    }

    // ===== Change Password =====
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestBody PasswordChangeRequest request) {

        Admin admin = adminRepository.findById(1L).orElse(null);
        if (admin == null) {
            return ResponseEntity.badRequest().body("Admin not found");
        }

        if (!admin.getPassword().equals(request.getCurrentPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("New passwords do not match");
        }

        admin.setPassword(request.getNewPassword());
        adminRepository.save(admin);

        return ResponseEntity.ok("Password changed successfully");
    }
}