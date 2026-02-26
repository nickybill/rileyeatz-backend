package com.rileyeatz.backend.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Restaurant {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    // ===== Optional: bidirectional mapping =====
    @OneToOne(mappedBy = "restaurant")
    private Admin admin;

    // ===== Getters & Setters =====
    public UUID getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    public void setAddress(String address) {

    }

    public void setPhone(String phone) {

    }
}