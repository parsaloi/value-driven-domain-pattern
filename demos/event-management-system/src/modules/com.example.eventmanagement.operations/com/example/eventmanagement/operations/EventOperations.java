package com.example.eventmanagement.operations;

import com.example.eventmanagement.domain.Event;
import com.example.eventmanagement.domain.Concert;
import com.example.eventmanagement.domain.Conference;
import com.example.eventmanagement.domain.Exhibition;
import com.example.eventmanagement.domain.Workshop;
import com.example.eventmanagement.domain.Money;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public final class EventOperations {
    private EventOperations() {}

    public static List<Event> searchEvents(List<Event> events, Predicate<Event> criteria) {
        return events.stream().filter(criteria).toList();
    }

    public static List<Event> findEventsByTimeRange(List<Event> events, LocalDateTime start, LocalDateTime end) {
        return searchEvents(events, e -> !e.startTime().isBefore(start) && !e.endTime().isAfter(end));
    }

    public static boolean hasAvailableCapacity(Event event, int currentAttendees) {
        return currentAttendees < event.maxAttendees();
    }

    public static Money getEventFee(Event event) {
        return switch (event) {
            case Concert c -> c.ticketPrice();
            case Conference c -> c.registrationFee();
            case Exhibition e -> e.entryFee();
            case Workshop w -> w.participationFee();
        };
    }
}