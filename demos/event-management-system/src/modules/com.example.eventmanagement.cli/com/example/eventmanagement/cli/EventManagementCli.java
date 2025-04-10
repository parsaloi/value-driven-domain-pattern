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
            System.out.println("\nOptions: 1) Create Event 2) Register Attendee 3) View Revenue 4) List Events " +
                    "5) Exit 6) View Event Info 7) View Event Attendees");
            System.out.print("Choose an option: ");
            int choice = SCANNER.nextInt();
            SCANNER.nextLine();

            switch (choice) {
                case 1 -> events.add(createEvent());
                case 2 -> registerAttendee(events, registrations);
                case 3 -> viewRevenue(registrations);
                case 4 -> listEvents(events);
                case 5 -> { System.out.println("Exiting..."); return; }
                case 6 -> viewEventInfo(events);
                case 7 -> viewEventAttendees(events, registrations);
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
        for (int i = 0; i < events.size(); i++) {
            System.out.println(i + ": " + events.get(i).name() + " (" + events.get(i).startTime().format(DATE_FORMAT) +
                    " - " + events.get(i).endTime().format(DATE_FORMAT) + ") - " + eventType(events.get(i)));
        }
    }

    private static void viewEventInfo(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        listEvents(events);
        System.out.print("Select Event Index to View Info: ");
        int eventIdx = SCANNER.nextInt();
        SCANNER.nextLine();
        if (eventIdx < 0 || eventIdx >= events.size()) {
            System.out.println("Invalid event index.");
            return;
        }

        Event event = events.get(eventIdx);
        System.out.println("\nEvent Information:");
        System.out.println("Name: " + event.name());
        System.out.println("Start Time: " + event.startTime().format(DATE_FORMAT));
        System.out.println("End Time: " + event.endTime().format(DATE_FORMAT));
        System.out.println("Location: " + event.location().name() + " (" + event.location().address() + ")");
        System.out.println("Max Attendees: " + event.maxAttendees());

        switch (event) {
            case Concert concert -> {
                System.out.println("Artist: " + concert.artist());
                System.out.println("Genre: " + concert.genre());
                System.out.println("Ticket Price: " + concert.ticketPrice().amount() + " " + concert.ticketPrice().currency());
            }
            case Conference conference -> {
                System.out.println("Speakers: " + String.join(", ", conference.speakers()));
                System.out.println("Topics: " + String.join(", ", conference.topics()));
                System.out.println("Registration Fee: " + conference.registrationFee().amount() + " " + conference.registrationFee().currency());
            }
            case Exhibition exhibition -> {
                System.out.println("Theme: " + exhibition.theme());
                System.out.println("Exhibitors: " + String.join(", ", exhibition.exhibitors()));
                System.out.println("Entry Fee: " + exhibition.entryFee().amount() + " " + exhibition.entryFee().currency());
            }
            case Workshop workshop -> {
                System.out.println("Instructor: " + workshop.instructor());
                System.out.println("Skill Level: " + workshop.skillLevel());
                System.out.println("Participation Fee: " + workshop.participationFee().amount() + " " + workshop.participationFee().currency());
                System.out.println("Max Participants: " + workshop.maxParticipants());
            }
        }
    }

    private static void viewEventAttendees(List<Event> events, List<Registration> registrations) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        listEvents(events);
        System.out.print("Select Event Index to View Attendees: ");
        int eventIdx = SCANNER.nextInt();
        SCANNER.nextLine();
        if (eventIdx < 0 || eventIdx >= events.size()) {
            System.out.println("Invalid event index.");
            return;
        }

        Event event = events.get(eventIdx);
        List<Registration> eventRegistrations = registrations.stream()
                .filter(reg -> reg.event().id().equals(event.id()))
                .toList();
        if (eventRegistrations.isEmpty()) {
            System.out.println("No attendees registered for " + event.name() + ".");
            return;
        }

        System.out.println("\nAttendees for " + event.name() + ":");
        for (Registration reg : eventRegistrations) {
            Attendee attendee = reg.attendee();
            System.out.println("Name: " + attendee.name() + ", Email: " + attendee.email() + ", Phone: " + attendee.phone());
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
