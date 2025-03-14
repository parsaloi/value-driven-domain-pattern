package com.example.eventmanagement.domain;

import java.time.LocalDateTime;

public final class EventValidations {
    private EventValidations() {}

    public static void validateEvent(String name, LocalDateTime startTime, LocalDateTime endTime,
                                     Location location, int maxAttendees) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Event name cannot be null or blank");
        if (startTime == null) throw new IllegalArgumentException("Start time cannot be null");
        if (endTime == null) throw new IllegalArgumentException("End time cannot be null");
        if (endTime.isBefore(startTime)) throw new IllegalArgumentException("End time cannot be before start time");
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        if (maxAttendees <= 0) throw new IllegalArgumentException("Maximum attendees must be positive");
    }
}