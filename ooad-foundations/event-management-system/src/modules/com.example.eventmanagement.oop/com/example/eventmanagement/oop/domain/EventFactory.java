package com.example.eventmanagement.oop.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;

public class EventFactory {
    private static final Currency USD = Currency.getInstance("USD");

    public static Event createConcert(String name, LocalDateTime startTime, LocalDateTime endTime, Location location,
                                      int maxAttendees, String artist, String genre, BigDecimal ticketPrice) {
        return new Concert(name, startTime, endTime, location, maxAttendees, artist, genre, new Money(ticketPrice, USD));
    }

    public static Event createConference(String name, LocalDateTime startTime, LocalDateTime endTime, Location location,
                                         int maxAttendees, List<String> speakers, List<String> topics, BigDecimal fee) {
        return new Conference(name, startTime, endTime, location, maxAttendees, speakers, topics, new Money(fee, USD));
    }

    public static Event createExhibition(String name, LocalDateTime startTime, LocalDateTime endTime, Location location,
                                         int maxAttendees, String theme, List<String> exhibitors, BigDecimal fee) {
        return new Exhibition(name, startTime, endTime, location, maxAttendees, theme, exhibitors, new Money(fee, USD));
    }

    public static Event createWorkshop(String name, LocalDateTime startTime, LocalDateTime endTime, Location location,
                                       int maxAttendees, String instructor, String skillLevel, BigDecimal fee, int maxParticipants) {
        return new Workshop(name, startTime, endTime, location, maxAttendees, instructor, skillLevel, new Money(fee, USD), maxParticipants);
    }
}
