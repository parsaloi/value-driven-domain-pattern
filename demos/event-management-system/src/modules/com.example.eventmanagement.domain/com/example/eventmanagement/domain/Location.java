package com.example.eventmanagement.domain;

public record Location(String name, String address) {
    public Location {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Location name cannot be null or blank");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address cannot be null or blank");
    }
}