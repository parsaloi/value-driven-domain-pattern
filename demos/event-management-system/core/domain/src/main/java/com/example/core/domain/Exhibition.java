package com.example.core.domain;

import java.time.LocalDateTime;
import java.util.List;

public record Exhibition(EventId id, String name, LocalDateTime startTime, LocalDateTime endTime,
                         Location location, int maxAttendees, String theme, List<String> exhibitors,
                         Money entryFee) implements Event {
    public Exhibition {
        if (theme == null || theme.isBlank()) throw new IllegalArgumentException("Theme cannot be null or blank");
        if (exhibitors == null || exhibitors.isEmpty()) throw new IllegalArgumentException("Exhibitors list cannot be null or empty");
        EventValidations.validateEvent(name, startTime, endTime, location, maxAttendees);
        exhibitors = List.copyOf(exhibitors);
    }
}