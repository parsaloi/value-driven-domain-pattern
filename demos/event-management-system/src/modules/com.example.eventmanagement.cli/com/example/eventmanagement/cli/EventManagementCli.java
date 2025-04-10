package com.example.eventmanagement.cli;

import com.example.eventmanagement.domain.*;
import com.example.eventmanagement.operations.EventOperations;
import com.example.eventmanagement.operations.RegistrationOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EventManagementCli {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Currency USD = Currency.getInstance("USD");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        List<Registration> registrations = new ArrayList<>();

        System.out.println("Welcome to Event Organizer CLI");
        while (true) {
            System.out.println("\nOptions: 1) Create Event 2) Register Attendee 3) View Revenue 4) List Events 5) Exit");
            System.out.print("Choose an option: ");
            int choice = SCANNER.nextInt();
            SCANNER.nextLine();

            switch (choice) {
                case 1 -> events.add(createEvent());
                case 2 -> registerAttendee(events, registrations);
                case 3 -> viewRevenue(registrations);
                case 4 -> listEvents(events);
                case 5 -> { System.out.println("Exiting..."); return; }
                default -> System.out.println("Invalid option");
            }
        }
    }

    private static Event createEvent() {
        System.out.println("Event Types: 1) Concert 2) Conference 3) Exhibition 4) Workshop");
        System.out.print("Select Event Type: ");
        int type = SCANNER.nextInt();
        SCANNER.nextLine();

        System.out.print("Event Name: ");
        String name = SCANNER.nextLine();
        System.out.print("Start Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime start = LocalDateTime.parse(SCANNER.nextLine(), DATE_FORMAT);
        System.out.print("End Time (yyyy-MM-dd HH:mm): ");
        LocalDateTime end = LocalDateTime.parse(SCANNER.nextLine(), DATE_FORMAT);
        System.out.print("Venue Name: ");
        String venue = SCANNER.nextLine();
        System.out.print("Venue Address: ");
        String address = SCANNER.nextLine();
        System.out.print("Max Attendees: ");
        int maxAttendees = SCANNER.nextInt();
        SCANNER.nextLine();

        return switch (type) {
            case 1 -> {
                System.out.print("Artist: ");
                String artist = SCANNER.nextLine();
                System.out.print("Genre: ");
                String genre = SCANNER.nextLine();
                System.out.print("Ticket Price (USD): ");
                BigDecimal price = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                yield new Concert(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, artist, genre, new Money(price, USD));
            }
            case 2 -> {
                System.out.print("Speakers (comma-separated names): ");
                List<String> speakers = Arrays.asList(SCANNER.nextLine().split(",\\s*"));
                System.out.print("Topics (comma-separated): ");
                List<String> topics = Arrays.asList(SCANNER.nextLine().split(",\\s*"));
                System.out.print("Registration Fee (USD): ");
                BigDecimal fee = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                yield new Conference(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, speakers, topics, new Money(fee, USD));
            }
            case 3 -> {
                System.out.print("Theme: ");
                String theme = SCANNER.nextLine();
                System.out.print("Exhibitors (comma-separated names): ");
                List<String> exhibitors = Arrays.asList(SCANNER.nextLine().split(",\\s*"));
                System.out.print("Entry Fee (USD): ");
                BigDecimal fee = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                yield new Exhibition(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, theme, exhibitors, new Money(fee, USD));
            }
            case 4 -> {
                System.out.print("Instructor: ");
                String instructor = SCANNER.nextLine();
                System.out.print("Skill Level: ");
                String skillLevel = SCANNER.nextLine();
                System.out.print("Participation Fee (USD): ");
                BigDecimal fee = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                System.out.print("Max Participants: ");
                int maxParticipants = SCANNER.nextInt();
                SCANNER.nextLine();
                yield new Workshop(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, instructor, skillLevel, new Money(fee, USD), maxParticipants);
            }
            default -> throw new IllegalArgumentException("Invalid event type");
        };
    }

    private static void registerAttendee(List<Event> events, List<Registration> registrations) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        System.out.println("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println(i + ": " + events.get(i).name() + " (" + eventType(events.get(i)) + ")");
        }
        System.out.print("Select Event Index: ");
        int eventIdx = SCANNER.nextInt();
        SCANNER.nextLine();
        if (eventIdx < 0 || eventIdx >= events.size()) {
            System.out.println("Invalid event index.");
            return;
        }

        System.out.print("Attendee Name: ");
        String name = SCANNER.nextLine();
        System.out.print("Email: ");
        String email = SCANNER.nextLine();
        System.out.print("Phone: ");
        String phone = SCANNER.nextLine();

        Attendee attendee = new Attendee(UUID.randomUUID(), name, email, phone);
        Event event = events.get(eventIdx);
        var regOpt = RegistrationOperations.registerForEvent(event, attendee, registrations);
        if (regOpt.isPresent()) {
            registrations.add(regOpt.get());
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed: No capacity.");
        }
    }

    private static void viewRevenue(List<Registration> registrations) {
        Money revenue = RegistrationOperations.calculateTotalRevenue(registrations);
        System.out.println("Total Revenue: " + revenue.amount() + " " + revenue.currency());
    }

    private static void listEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events.");
            return;
        }
        for (Event e : events) {
            System.out.println(e.name() + " (" + e.startTime().format(DATE_FORMAT) + " - " +
                    e.endTime().format(DATE_FORMAT) + ") - " + eventType(e));
        }
    }

    private static String eventType(Event event) {
        return switch (event) {
            case Concert ignored -> "Concert";
            case Conference ignored -> "Conference";
            case Exhibition ignored -> "Exhibition";
            case Workshop ignored -> "Workshop";
            default -> "Unknown";
        };
    }
}
