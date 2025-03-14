package com.example.eventmanagement.domain;

import java.time.LocalDateTime;

public record Concert(EventId id, String name, LocalDateTime startTime, LocalDateTime endTime,
                      Location location, int maxAttendees, String artist, String genre, Money ticketPrice)
        implements Event {
    public Concert {
        if (artist == null || artist.isBlank()) throw new IllegalArgumentException("Artist cannot be null or blank");
        EventValidations.validateEvent(name, startTime, endTime, location, maxAttendees);
    }
}