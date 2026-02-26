package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.dto.LoginRequest;
import com.rileyeatz.backend.dto.LoginResponse;
import com.rileyeatz.backend.model.Admin;
import com.rileyeatz.backend.repository.AdminRepository;
import com.rileyeatz.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Optional<Admin> adminOpt = adminRepository.findByEmail(request.getEmail());
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();

            if (passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                String token = jwtUtil.generateToken(admin.getEmail());

                // Return restaurantId as String (UUID)
                String restaurantId = (admin.getRestaurant() != null)
                        ? admin.getRestaurant().getId().toString()
                        : null;

                return ResponseEntity.ok(new LoginResponse(token, restaurantId));
            } else {
                return ResponseEntity.badRequest()
                        .body(new LoginResponse("Invalid email or password", null));
            }
        }

        return ResponseEntity.badRequest()
                .body(new LoginResponse("Invalid email or password", null));
    }
}