package com.example.core.domain;

import java.util.UUID;

public record EventId(UUID value) {
    public EventId {
        if (value == null) throw new IllegalArgumentException("Event ID cannot be null");
    }
    public static EventId generate() { return new EventId(UUID.randomUUID()); }
}
