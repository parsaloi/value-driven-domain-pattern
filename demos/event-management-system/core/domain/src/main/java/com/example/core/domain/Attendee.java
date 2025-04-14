package com.example.core.domain;

import java.util.UUID;

public record Attendee(UUID id, String name, String email, String phone) {
    public Attendee {
        if (id == null) throw new IllegalArgumentException("Attendee ID cannot be null");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank");
        if (email == null || email.isBlank() || !email.contains("@")) throw new IllegalArgumentException("Valid email is required");
    }
}