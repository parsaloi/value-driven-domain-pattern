package com.example.core.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Registration(UUID id, Event event, Attendee attendee, LocalDateTime registrationTime,
                           RegistrationStatus status) {
    public Registration {
        if (id == null) throw new IllegalArgumentException("Registration ID cannot be null");
        if (event == null) throw new IllegalArgumentException("Event cannot be null");
        if (attendee == null) throw new IllegalArgumentException("Attendee cannot be null");
        if (registrationTime == null) throw new IllegalArgumentException("Registration time cannot be null");
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
    }
}
