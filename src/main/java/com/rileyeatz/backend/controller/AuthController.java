package com.rileyeatz.backend.controller;

import com.rileyeatz.backend.dto.RegisterRequest;
import com.rileyeatz.backend.dto.LoginRequest;
import com.rileyeatz.backend.dto.LoginResponse;
import com.rileyeatz.backend.model.User;
import com.rileyeatz.backend.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // ================= REGISTER =================
    @PostMapping(
            value = "/register",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Email already exists"));
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return ResponseEntity.ok(new LoginResponse("User registered successfully"));
    }

    // ================= LOGIN =================
    @PostMapping(
            value = "/login",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Invalid email or password"));
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponse("Invalid email or password"));
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return ResponseEntity.ok(new LoginResponse(token));
    }
}