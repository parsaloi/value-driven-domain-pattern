package com.example.eventmanagement.domain;

import java.time.LocalDateTime;
import java.util.List;

public record Conference(EventId id, String name, LocalDateTime startTime, LocalDateTime endTime,
                         Location location, int maxAttendees, List<String> speakers, List<String> topics,
                         Money registrationFee) implements Event {
    public Conference {
        if (speakers == null || speakers.isEmpty()) throw new IllegalArgumentException("Speakers list cannot be null or empty");
        if (topics == null || topics.isEmpty()) throw new IllegalArgumentException("Topics list cannot be null or empty");
        EventValidations.validateEvent(name, startTime, endTime, location, maxAttendees);
        speakers = List.copyOf(speakers);
        topics = List.copyOf(topics);
    }
}