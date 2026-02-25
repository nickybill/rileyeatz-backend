package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.model.Admin;
import com.rileyeatz.backend.payload.PasswordChangeRequest;
import com.rileyeatz.backend.repository.AdminRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private Cloudinary cloudinary;

    // ===== Dashboard =====
    @GetMapping("/dashboard")
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("pending", 12);
        stats.put("preparing", 5);
        stats.put("delivered", 30);
        stats.put("cancelled", 2);
        return stats;
    }

    // ===== Get Admin Profile by ID =====
    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ResponseEntity.badRequest().body("Admin not found");
        return ResponseEntity.ok(admin);
    }

    // ===== List All Admins =====
    @GetMapping("/")
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminRepository.findAll());
    }

    // ===== Create Admin =====
    @PostMapping("/")
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin newAdmin) {
        return ResponseEntity.ok(adminRepository.save(newAdmin));
    }

    // ===== Delete Admin =====
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        if (!adminRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Admin not found");
        }
        adminRepository.deleteById(id);
        return ResponseEntity.ok("Admin deleted successfully");
    }

    // ===== Update Admin Profile (Text Only JSON) =====
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdminProfile(
            @PathVariable Long id,
            @RequestBody Admin updatedAdmin) {

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ResponseEntity.badRequest().body("Admin not found");

        admin.setName(updatedAdmin.getName());
        admin.setEmail(updatedAdmin.getEmail());
        admin.setPhone(updatedAdmin.getPhone());

        adminRepository.save(admin);
        return ResponseEntity.ok(admin);
    }

    // ===== Update Admin Profile with Image (Multipart) =====
    @PutMapping(value = "/{id}/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateAdminProfileWithImage(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ResponseEntity.badRequest().body("Admin not found");

        admin.setName(name);
        admin.setEmail(email);

        if (image != null && !image.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                String imageUrl = uploadResult.get("secure_url").toString();
                admin.setImageUrl(imageUrl);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Image upload failed: " + e.getMessage());
            }
        }

        adminRepository.save(admin);
        return ResponseEntity.ok(admin);
    }

    // ===== Change Password =====
    @PutMapping("/{id}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody PasswordChangeRequest request) {

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin == null) return ResponseEntity.badRequest().body("Admin not found");

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