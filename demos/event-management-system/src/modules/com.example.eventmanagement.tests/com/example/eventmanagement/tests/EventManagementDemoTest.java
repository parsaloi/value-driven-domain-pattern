package com.example.eventmanagement.tests;

import com.example.core.common.utilities.operations.OperationContext;
import com.example.core.common.utilities.operations.OperationsTaskScope;
import com.example.core.common.utilities.completion.OperationResult;
import com.example.core.common.utilities.completion.TaskCompletionHandler;
import com.example.eventmanagement.domain.*;
import com.example.eventmanagement.operations.EventOperations;
import com.example.eventmanagement.operations.MoneyOperations;
import com.example.eventmanagement.operations.RegistrationOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Property-based test suite with type safety and robust data generation.
 */
public class EventManagementDemoTest {
    private static final Random RANDOM = new Random();
    private static final Currency USD = Currency.getInstance("USD");
    private static final int GENERATION_LIMIT = 100;
    private static final double INVALID_PROBABILITY = 0.3;

    private static final Map<String, AtomicInteger> VALID_COUNTS = new HashMap<>();
    private static final Map<String, AtomicInteger> INVALID_COUNTS = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.out.println("Running property-based tests...");
        testRegistrationCapacity();
        testRevenueCalculation();
        testEventTimeRangeFilter();
        testEventTypeSafety();
        printSummaryReport();
        System.out.println("All property tests passed!");
    }

    // --- Property Tests ---

    private static void testRegistrationCapacity() throws InterruptedException, ExecutionException {
        System.out.println("Testing registration capacity...");
        Concert concert = generateRandomConcert("Capacity Test Concerts");
        List<Registration> registrations = new ArrayList<>();
        int maxAttendees = concert.maxAttendees();

        for (int i = 0; i < maxAttendees; i++) {
            Attendee attendee = generateRandomAttendee("Capacity Test Attendees");
            try (var scope = new OperationsTaskScope<>("RegisterTest", new OperationContext.NonTransactional(),
                    new TaskCompletionHandler<Registration>() {
                        @Override public void onSuccess(Registration value) { registrations.add(value); }
                        @Override public void onFailure(Throwable t) { throw new AssertionError("Unexpected failure: " + t); }
                    })) {
                scope.fork(() -> {
                    var regOpt = RegistrationOperations.registerForEvent(concert, attendee, registrations);
                    if (regOpt.isPresent()) {
                        return new OperationResult.Success<>(regOpt.get());
                    } else {
                        return new OperationResult.Failure<>(new IllegalStateException("No capacity"));
                    }
                });
                scope.join();
            }
        }
        assert registrations.size() == maxAttendees : "Should reach max capacity: " + registrations.size();

        Attendee extraAttendee = generateRandomAttendee("Over Capacity Attendees");
        try (var scope = new OperationsTaskScope<>("OverCapacityTest", new OperationContext.NonTransactional(),
                new TaskCompletionHandler<Registration>() {
                    @Override public void onSuccess(Registration value) { throw new AssertionError("Should not register beyond capacity"); }
                    @Override public void onFailure(Throwable t) { /* Expected */ }
                })) {
            scope.fork(() -> {
                var regOpt = RegistrationOperations.registerForEvent(concert, extraAttendee, registrations);
                if (regOpt.isPresent()) {
                    return new OperationResult.Success<>(regOpt.get());
                } else {
                    return new OperationResult.Failure<>(new IllegalStateException("No capacity"));
                }
            });
            scope.join();
        }
        assert registrations.size() == maxAttendees : "Capacity should not exceed max: " + registrations.size();
        System.out.println("Capacity test passed!");
    }

    private static void testRevenueCalculation() throws InterruptedException, ExecutionException {
        System.out.println("Testing revenue calculation...");
        Concert concert = generateRandomConcert("Revenue Test Concerts");
        List<Registration> registrations = new ArrayList<>();
        int numRegistrations = RANDOM.nextInt(concert.maxAttendees() + 1);

        try (var scope = new OperationsTaskScope<>("GenerateRegistrations", new OperationContext.NonTransactional(),
                new TaskCompletionHandler<Registration>() {
                    @Override public void onSuccess(Registration value) { registrations.add(value); }
                    @Override public void onFailure(Throwable t) { throw new AssertionError("Unexpected failure: " + t); }
                })) {
            for (int i = 0; i < numRegistrations; i++) {
                scope.fork(() -> new OperationResult.Success<>(RegistrationOperations.createRegistration(
                        concert, generateRandomAttendee("Revenue Test Attendees"),
                        RegistrationStatus.values()[RANDOM.nextInt(RegistrationStatus.values().length)])));
            }
            scope.join();
        }

        Money expectedRevenue = MoneyOperations.sum(
                registrations.stream()
                        .filter(r -> r.status() == RegistrationStatus.CONFIRMED || r.status() == RegistrationStatus.ATTENDED)
                        .map(r -> concert.ticketPrice())
                        .collect(Collectors.toList())
        );
        Money actualRevenue = RegistrationOperations.calculateTotalRevenue(registrations);
        assert expectedRevenue.amount().equals(actualRevenue.amount()) : "Revenue mismatch: expected " + expectedRevenue + ", got " + actualRevenue;
        System.out.println("Revenue test passed!");
    }

    private static void testEventTimeRangeFilter() throws InterruptedException, ExecutionException {
        System.out.println("Testing event time range filter...");
        List<Event> events = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        try (var scope = new OperationsTaskScope<>("GenerateEvents", new OperationContext.NonTransactional(),
                new TaskCompletionHandler<Event>() {
                    @Override public void onSuccess(Event value) { events.add(value); }
                    @Override public void onFailure(Throwable t) { throw new AssertionError("Unexpected failure: " + t); }
                })) {
            scope.fork(() -> new OperationResult.Success<>(generateRandomConcert("Time Range Test Concerts")));
            scope.fork(() -> new OperationResult.Success<>(generateRandomConference("Time Range Test Conferences")));
            scope.fork(() -> new OperationResult.Success<>(generateRandomExhibition("Time Range Test Exhibitions")));
            scope.fork(() -> new OperationResult.Success<>(generateRandomWorkshop("Time Range Test Workshops")));
            scope.join();
        }

        LocalDateTime start = now.plusDays(RANDOM.nextInt(5));
        LocalDateTime end = start.plusDays(RANDOM.nextInt(5) + 1);
        List<Event> filtered = EventOperations.findEventsByTimeRange(events, start, end);
        for (Event e : filtered) {
            assert !e.startTime().isBefore(start) && !e.endTime().isAfter(end) : "Event " + e + " outside range " + start + " to " + end;
        }
        System.out.println("Time range filter test passed!");
    }

    private static void testEventTypeSafety() {
        System.out.println("Testing event type safety...");
        // Attempt invalid constructions and verify they fail
        try {
            new Concert(EventId.generate(), "Invalid", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                    new Location("V", "A"), 10, "", "Rock", new Money(BigDecimal.TEN, USD));
            throw new AssertionError("Concert should reject blank artist");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new Conference(EventId.generate(), "Invalid", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                    new Location("V", "A"), 10, List.of(), List.of("Java"), new Money(BigDecimal.TEN, USD));
            throw new AssertionError("Conference should reject empty speakers");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new Exhibition(EventId.generate(), "Invalid", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                    new Location("V", "A"), 10, "", List.of("Ex1"), new Money(BigDecimal.TEN, USD));
            throw new AssertionError("Exhibition should reject blank theme");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            new Workshop(EventId.generate(), "Invalid", LocalDateTime.now(), LocalDateTime.now().plusHours(1),
                    new Location("V", "A"), 10, "", "Beginner", new Money(BigDecimal.TEN, USD), 5);
            throw new AssertionError("Workshop should reject blank instructor");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        System.out.println("Type safety test passed!");
    }

    // --- Data Generators ---

    private static Concert generateRandomConcert(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Concert concert = null;
        for (int i = 0; i < GENERATION_LIMIT && concert == null; i++) {
            try {
                LocalDateTime start = LocalDateTime.now().plusDays(RANDOM.nextInt(10) - 5);
                LocalDateTime end = Math.random() < INVALID_PROBABILITY
                        ? start.minusHours(RANDOM.nextInt(5) + 1)
                        : start.plusHours(RANDOM.nextInt(5) + 1);
                concert = new Concert(
                        EventId.generate(),
                        "Concert-" + RANDOM.nextInt(1000),
                        start, end,
                        generateRandomLocation(statKey + " Locations"),
                        RANDOM.nextInt(50) + 1,
                        Math.random() < INVALID_PROBABILITY ? "" : "Artist-" + RANDOM.nextInt(100),
                        "Genre-" + RANDOM.nextInt(10),
                        generateRandomMoney(statKey + " Ticket Prices")
                );
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (concert == null) throw new RuntimeException("Failed to generate valid concert for " + statKey);
        return concert;
    }

    private static Conference generateRandomConference(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Conference conference = null;
        for (int i = 0; i < GENERATION_LIMIT && conference == null; i++) {
            try {
                LocalDateTime start = LocalDateTime.now().plusDays(RANDOM.nextInt(10) - 5);
                LocalDateTime end = Math.random() < INVALID_PROBABILITY
                        ? start.minusHours(RANDOM.nextInt(5) + 1)
                        : start.plusHours(RANDOM.nextInt(5) + 1);
                List<String> speakers = Math.random() < INVALID_PROBABILITY
                        ? List.of()
                        : List.of("Speaker-" + RANDOM.nextInt(100));
                conference = new Conference(
                        EventId.generate(),
                        "Conference-" + RANDOM.nextInt(1000),
                        start, end,
                        generateRandomLocation(statKey + " Locations"),
                        RANDOM.nextInt(50) + 1,
                        speakers,
                        List.of("Topic-" + RANDOM.nextInt(10)),
                        generateRandomMoney(statKey + " Fees")
                );
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (conference == null) throw new RuntimeException("Failed to generate valid conference for " + statKey);
        return conference;
    }

    private static Exhibition generateRandomExhibition(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Exhibition exhibition = null;
        for (int i = 0; i < GENERATION_LIMIT && exhibition == null; i++) {
            try {
                LocalDateTime start = LocalDateTime.now().plusDays(RANDOM.nextInt(10) - 5);
                LocalDateTime end = Math.random() < INVALID_PROBABILITY
                        ? start.minusHours(RANDOM.nextInt(5) + 1)
                        : start.plusHours(RANDOM.nextInt(5) + 1);
                String theme = Math.random() < INVALID_PROBABILITY ? "" : "Theme-" + RANDOM.nextInt(10);
                exhibition = new Exhibition(
                        EventId.generate(),
                        "Exhibition-" + RANDOM.nextInt(1000),
                        start, end,
                        generateRandomLocation(statKey + " Locations"),
                        RANDOM.nextInt(50) + 1,
                        theme,
                        List.of("Exhibitor-" + RANDOM.nextInt(100)),
                        generateRandomMoney(statKey + " Fees")
                );
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (exhibition == null) throw new RuntimeException("Failed to generate valid exhibition for " + statKey);
        return exhibition;
    }

    private static Workshop generateRandomWorkshop(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Workshop workshop = null;
        for (int i = 0; i < GENERATION_LIMIT && workshop == null; i++) {
            try {
                LocalDateTime start = LocalDateTime.now().plusDays(RANDOM.nextInt(10) - 5);
                LocalDateTime end = Math.random() < INVALID_PROBABILITY
                        ? start.minusHours(RANDOM.nextInt(5) + 1)
                        : start.plusHours(RANDOM.nextInt(5) + 1);
                String instructor = Math.random() < INVALID_PROBABILITY ? "" : "Instructor-" + RANDOM.nextInt(100);
                workshop = new Workshop(
                        EventId.generate(),
                        "Workshop-" + RANDOM.nextInt(1000),
                        start, end,
                        generateRandomLocation(statKey + " Locations"),
                        RANDOM.nextInt(50) + 1,
                        instructor,
                        "Level-" + RANDOM.nextInt(3),
                        generateRandomMoney(statKey + " Fees"),
                        RANDOM.nextInt(20) + 1
                );
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (workshop == null) throw new RuntimeException("Failed to generate valid workshop for " + statKey);
        return workshop;
    }

    private static Attendee generateRandomAttendee(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Attendee attendee = null;
        for (int i = 0; i < GENERATION_LIMIT && attendee == null; i++) {
            try {
                String email = Math.random() < INVALID_PROBABILITY
                        ? "invalid-email"
                        : "user" + RANDOM.nextInt(1000) + "@example.com";
                attendee = new Attendee(
                        UUID.randomUUID(),
                        "Attendee-" + RANDOM.nextInt(1000),
                        email,
                        "555-" + String.format("%04d", RANDOM.nextInt(10000))
                );
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (attendee == null) throw new RuntimeException("Failed to generate valid attendee for " + statKey);
        return attendee;
    }

    private static Location generateRandomLocation(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Location location = null;
        for (int i = 0; i < GENERATION_LIMIT && location == null; i++) {
            try {
                String name = Math.random() < INVALID_PROBABILITY ? "" : "Venue-" + RANDOM.nextInt(100);
                String address = Math.random() < INVALID_PROBABILITY ? "" : "Addr-" + RANDOM.nextInt(1000);
                location = new Location(name, address);
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (location == null) throw new RuntimeException("Failed to generate valid location for " + statKey);
        return location;
    }

    private static Money generateRandomMoney(String statKey) {
        VALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        INVALID_COUNTS.putIfAbsent(statKey, new AtomicInteger(0));
        Money money = null;
        for (int i = 0; i < GENERATION_LIMIT && money == null; i++) {
            try {
                BigDecimal amount = Math.random() < INVALID_PROBABILITY
                        ? BigDecimal.valueOf(-RANDOM.nextDouble() * 100)
                        : BigDecimal.valueOf(RANDOM.nextDouble() * 100);
                money = new Money(amount, USD);
                VALID_COUNTS.get(statKey).incrementAndGet();
            } catch (IllegalArgumentException e) {
                INVALID_COUNTS.get(statKey).incrementAndGet();
            }
        }
        if (money == null) throw new RuntimeException("Failed to generate valid money for " + statKey);
        return money;
    }

    // --- Summary Report ---

    private static void printSummaryReport() {
        System.out.println("\nSummary Report of Data Generation:");
        for (String key : VALID_COUNTS.keySet()) {
            int valid = VALID_COUNTS.get(key).get();
            int invalid = INVALID_COUNTS.get(key).get();
            System.out.printf("%s: Valid=%d, Invalid=%d%n", key, valid, invalid);
        }
    }
}
