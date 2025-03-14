package com.example.eventmanagement.domain;

import java.time.LocalDateTime;

public record Workshop(EventId id, String name, LocalDateTime startTime, LocalDateTime endTime,
                       Location location, int maxAttendees, String instructor, String skillLevel,
                       Money participationFee, int maxParticipants) implements Event {
    public Workshop {
        if (instructor == null || instructor.isBlank()) throw new IllegalArgumentException("Instructor cannot be null or blank");
        if (skillLevel == null || skillLevel.isBlank()) throw new IllegalArgumentException("Skill level cannot be null or blank");
        if (maxParticipants <= 0) throw new IllegalArgumentException("Maximum participants must be positive");
        EventValidations.validateEvent(name, startTime, endTime, location, maxAttendees);
    }
}