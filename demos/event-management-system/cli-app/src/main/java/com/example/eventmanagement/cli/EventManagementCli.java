package com.example.eventmanagement.cli;

import com.example.core.domain.*;
import com.example.core.operations.EventOperations;
import com.example.core.operations.RegistrationOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public class EventManagementCli {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Currency USD = Currency.getInstance("USD");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final int MAX_RETRIES = 3;

    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        List<Registration> registrations = new ArrayList<>();

        System.out.println("Welcome to Event Organizer CLI");
        while (true) {
            try {
                displayMainMenu();
                int choice = readIntInput("Choose an option: ", 1, 7);

                switch (choice) {
                    case 1 -> events.add(createEvent());
                    case 2 -> registerAttendee(events, registrations);
                    case 3 -> viewRevenue(registrations);
                    case 4 -> listEvents(events);
                    case 5 -> {
                        System.out.println("Thank you for using Event Organizer CLI. Goodbye!");
                        return;
                    }
                    case 6 -> viewEventInfo(events);
                    case 7 -> viewEventAttendees(events, registrations);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + ". Please try again.");
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\nOptions:");
        System.out.println("1) Create Event");
        System.out.println("2) Register Attendee");
        System.out.println("3) View Revenue");
        System.out.println("4) List Events");
        System.out.println("5) Exit");
        System.out.println("6) View Event Info");
        System.out.println("7) View Event Attendees");
    }

    private static int readIntInput(String prompt, int min, int max) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.print(prompt);
                String input = SCANNER.nextLine().trim();
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
        throw new IllegalStateException("Maximum attempts reached. Returning to main menu.");
    }

    private static String readNonEmptyString(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
        throw new IllegalStateException("Maximum attempts reached for input.");
    }

    private static LocalDateTime readDateTime(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.print(prompt);
                String input = SCANNER.nextLine().trim();
                return LocalDateTime.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2025-04-15 14:30).");
            }
        }
        throw new IllegalStateException("Maximum attempts reached for date input.");
    }

    private static BigDecimal readPositiveBigDecimal(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.print(prompt);
                String input = SCANNER.nextLine().trim();
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    return value;
                }
                System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid positive number (e.g., 10.50).");
            }
        }
        throw new IllegalStateException("Maximum attempts reached for amount input.");
    }

    private static Event createEvent() {
        try {
            int type = readIntInput("Event Types: 1) Concert 2) Conference 3) Exhibition 4) Workshop\nSelect Event Type: ", 1, 4);
            String name = readNonEmptyString("Event Name: ");
            LocalDateTime start = readDateTime("Start Time (yyyy-MM-dd HH:mm): ");
            LocalDateTime end = readDateTime("End Time (yyyy-MM-dd HH:mm): ");
            if (end.isBefore(start)) {
                throw new IllegalArgumentException("End time cannot be before start time.");
            }
            String venue = readNonEmptyString("Venue Name: ");
            String address = readNonEmptyString("Venue Address: ");
            int maxAttendees = readIntInput("Max Attendees: ", 1, Integer.MAX_VALUE);

            return switch (type) {
                case 1 -> {
                    String artist = readNonEmptyString("Artist: ");
                    String genre = readNonEmptyString("Genre: ");
                    BigDecimal price = readPositiveBigDecimal("Ticket Price (USD): ");
                    yield new Concert(EventId.generate(), name, start, end, new Location(venue, address),
                            maxAttendees, artist, genre, new Money(price, USD));
                }
                case 2 -> {
                    List<String> speakers = readNonEmptyList("Speakers (comma-separated names): ");
                    List<String> topics = readNonEmptyList("Topics (comma-separated): ");
                    BigDecimal fee = readPositiveBigDecimal("Registration Fee (USD): ");
                    yield new Conference(EventId.generate(), name, start, end, new Location(venue, address),
                            maxAttendees, speakers, topics, new Money(fee, USD));
                }
                case 3 -> {
                    String theme = readNonEmptyString("Theme: ");
                    List<String> exhibitors = readNonEmptyList("Exhibitors (comma-separated names): ");
                    BigDecimal fee = readPositiveBigDecimal("Entry Fee (USD): ");
                    yield new Exhibition(EventId.generate(), name, start, end, new Location(venue, address),
                            maxAttendees, theme, exhibitors, new Money(fee, USD));
                }
                case 4 -> {
                    String instructor = readNonEmptyString("Instructor: ");
                    String skillLevel = readNonEmptyString("Skill Level: ");
                    BigDecimal fee = readPositiveBigDecimal("Participation Fee (USD): ");
                    int maxParticipants = readIntInput("Max Participants: ", 1, Integer.MAX_VALUE);
                    yield new Workshop(EventId.generate(), name, start, end, new Location(venue, address),
                            maxAttendees, instructor, skillLevel, new Money(fee, USD), maxParticipants);
                }
                default -> throw new IllegalStateException("Unexpected event type.");
            };
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating event: " + e.getMessage());
            throw new IllegalStateException("Failed to create event after validation.");
        }
    }

    private static List<String> readNonEmptyList(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            List<String> items = Arrays.stream(input.split(",\\s*"))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .toList();
            if (!items.isEmpty()) {
                return items;
            }
            System.out.println("List cannot be empty. Please provide at least one item.");
        }
        throw new IllegalStateException("Maximum attempts reached for list input.");
    }

    private static void registerAttendee(List<Event> events, List<Registration> registrations) {
        if (events.isEmpty()) {
            System.out.println("No events available to register for.");
            return;
        }

        try {
            System.out.println("Available Events:");
            for (int i = 0; i < events.size(); i++) {
                System.out.println(i + ": " + events.get(i).name() + " (" + eventType(events.get(i)) + ")");
            }
            int eventIdx = readIntInput("Select Event Index: ", 0, events.size() - 1);

            String name = readNonEmptyString("Attendee Name: ");
            String email = readEmail("Email: ");
            String phone = readNonEmptyString("Phone: ");

            Attendee attendee = new Attendee(UUID.randomUUID(), name, email, phone);
            Event event = events.get(eventIdx);
            var regOpt = RegistrationOperations.registerForEvent(event, attendee, registrations);
            if (regOpt.isPresent()) {
                registrations.add(regOpt.get());
                System.out.println("Registration successful for " + event.name() + "!");
            } else {
                System.out.println("Registration failed: No capacity available for " + event.name() + ".");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error registering attendee: " + e.getMessage());
        }
    }

    private static String readEmail(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            if (input.contains("@") && !input.isBlank()) {
                return input;
            }
            System.out.println("Please enter a valid email address containing '@'.");
        }
        throw new IllegalStateException("Maximum attempts reached for email input.");
    }

    private static void viewRevenue(List<Registration> registrations) {
        Money revenue = RegistrationOperations.calculateTotalRevenue(registrations);
        System.out.println("Total Revenue: " + revenue.amount() + " " + revenue.currency());
    }

    private static void listEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        for (int i = 0; i < events.size(); i++) {
            System.out.println(i + ": " + events.get(i).name() + " (" +
                    events.get(i).startTime().format(DATE_FORMAT) + " - " +
                    events.get(i).endTime().format(DATE_FORMAT) + ") - " + eventType(events.get(i)));
        }
    }

    private static void viewEventInfo(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        try {
            listEvents(events);
            int eventIdx = readIntInput("Select Event Index to View Info: ", 0, events.size() - 1);

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
        } catch (IllegalArgumentException e) {
            System.out.println("Error viewing event info: " + e.getMessage());
        }
    }

    private static void viewEventAttendees(List<Event> events, List<Registration> registrations) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }

        try {
            listEvents(events);
            int eventIdx = readIntInput("Select Event Index to View Attendees: ", 0, events.size() - 1);

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
        } catch (IllegalArgumentException e) {
            System.out.println("Error viewing attendees: " + e.getMessage());
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