package com.example.eventmanagement.oop.operations;

import com.example.eventmanagement.oop.domain.Event;

import java.time.LocalDateTime;
import java.util.List;

public class EventOperations {
    public static boolean hasAvailableCapacity(Event event, int currentRegistrations) {
        return currentRegistrations < event.getMaxAttendees();
    }

    public static List<Event> findEventsByTimeRange(List<Event> events, LocalDateTime start, LocalDateTime end) {
        return events.stream()
                .filter(e -> !e.getStartTime().isBefore(start) && !e.getEndTime().isAfter(end))
                .toList();
    }
}
