package com.example.eventmanagement.cli;

import com.example.core.domain.*;
import com.example.core.operations.RegistrationOperations;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class CliPropertyTests {
    private static final Random RANDOM = ThreadLocalRandom.current();
    private static final Currency USD = Currency.getInstance("USD");

    public static void main(String[] args) {
        System.out.println("Running CLI property-based tests...");
        testEventCreation();
        testRegistrationFlow();
        System.out.println("All CLI property tests passed!");
    }

    // Property: Creating an event always results in a valid, non-null event
    private static void testEventCreation() {
        System.out.println("Testing event creation...");
        for (int i = 0; i < 100; i++) {
            Event event = createRandomEvent();
            assert event != null : "Event should not be null";
            assert event.id() != null : "Event ID should not be null";
            assert !event.name().isBlank() : "Event name should not be blank";
            assert event.startTime().isBefore(event.endTime()) : "Start time should be before end time";
            assert event.maxAttendees() > 0 : "Max attendees should be positive";
        }
        System.out.println("Event creation test passed!");
    }

    // Property: Registering attendees respects capacity and produces consistent registrations
    private static void testRegistrationFlow() {
        System.out.println("Testing registration flow...");
        Event event = createRandomEvent();
        List<Registration> registrations = new ArrayList<>();
        int maxAttendees = event.maxAttendees();

        // Register up to capacity
        for (int i = 0; i < maxAttendees; i++) {
            Attendee attendee = createRandomAttendee();
            Optional<Registration> regOpt = RegistrationOperations.registerForEvent(event, attendee, registrations);
            assert regOpt.isPresent() : "Registration should succeed within capacity";
            registrations.add(regOpt.get());
            assert registrations.size() == i + 1 : "Registration count should increment";
            assert regOpt.get().status() == RegistrationStatus.CONFIRMED : "New registration should be CONFIRMED";
        }

        // Try registering beyond capacity
        Attendee extraAttendee = createRandomAttendee();
        Optional<Registration> regOpt = RegistrationOperations.registerForEvent(event, extraAttendee, registrations);
        assert regOpt.isEmpty() : "Registration should fail beyond capacity";
        assert registrations.size() == maxAttendees : "Registration count should not exceed capacity";

        System.out.println("Registration flow test passed!");
    }

    private static Event createRandomEvent() {
        LocalDateTime start = LocalDateTime.now().plusDays(RANDOM.nextInt(10));
        return new Concert(
                EventId.generate(),
                "Test-Concert-" + RANDOM.nextInt(1000),
                start,
                start.plusHours(2),
                new Location("Venue-" + RANDOM.nextInt(100), "Addr-" + RANDOM.nextInt(1000)),
                RANDOM.nextInt(50) + 1,
                "Artist-" + RANDOM.nextInt(100),
                "Rock",
                new Money(BigDecimal.valueOf(RANDOM.nextDouble() * 100), USD)
        );
    }

    private static Attendee createRandomAttendee() {
        return new Attendee(
                UUID.randomUUID(),
                "Attendee-" + RANDOM.nextInt(1000),
                "user" + RANDOM.nextInt(1000) + "@example.com",
                "555-" + String.format("%04d", RANDOM.nextInt(10000))
        );
    }
}