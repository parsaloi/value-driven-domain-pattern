package com.example.eventmanagement.domain;

import java.time.LocalDateTime;

public sealed interface Event permits Concert, Conference, Exhibition, Workshop {
    EventId id();
    String name();
    LocalDateTime startTime();
    LocalDateTime endTime();
    Location location();
    int maxAttendees();
}