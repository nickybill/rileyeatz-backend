package com.rileyeatz.backend.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    @GetMapping("/dashboard")
    public Map<String, Integer> getDashboardStats() {

        Map<String, Integer> stats = new HashMap<>();

        stats.put("pending", 12);
        stats.put("preparing", 5);
        stats.put("delivered", 30);
        stats.put("cancelled", 2);

        return stats;
    }
}