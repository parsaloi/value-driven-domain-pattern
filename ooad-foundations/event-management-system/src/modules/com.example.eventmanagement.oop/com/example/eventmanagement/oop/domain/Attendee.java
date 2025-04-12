package com.example.eventmanagement.oop.domain;

import java.util.Objects;
import java.util.UUID;

public class Attendee {
    private UUID id;
    private String name;
    private String email;
    private String phone;

    public Attendee(String name, String email, String phone) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phone = phone;
        if (name == null || name.isBlank() || email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid attendee data");
        }
    }

    // Getters and setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Attendee attendee)) return false;
        return Objects.equals(id, attendee.id) &&
                Objects.equals(name, attendee.name) &&
                Objects.equals(email, attendee.email) &&
                Objects.equals(phone, attendee.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, phone);
    }

    @Override
    public String toString() {
        return "Attendee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
