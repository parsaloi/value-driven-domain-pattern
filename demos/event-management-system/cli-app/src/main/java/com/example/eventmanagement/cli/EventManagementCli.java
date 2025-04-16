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

        // Non-interactive mode if args are provided
        if (args.length > 0) {
            handleNonInteractiveMode(args, events, registrations);
            return;
        }

        // Interactive mode
        System.out.println("Welcome to Event Organizer CLI");
        while (true) {
            displayMainMenu();
            Integer choice = readIntInput("Choose an option: ", 1, 7);
            if (choice == null) {
                System.out.println("Invalid input or input stream closed. Returning to main menu.");
                if (!SCANNER.hasNextLine()) {
                    System.out.println("Input stream unavailable. Exiting.");
                    return;
                }
                continue;
            }

            switch (choice) {
                case 1 -> {
                    Event event = createEvent();
                    if (event != null) {
                        events.add(event);
                        System.out.println("Event created successfully!");
                    }
                }
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
        }
    }

    private static void handleNonInteractiveMode(String[] args, List<Event> events, List<Registration> registrations) {
        if (args.length == 1 && args[0].equals("--help")) {
            System.out.println("Event Organizer CLI: Non-Interactive Mode");
            System.out.println("Usage: java -jar cli-app.jar [command]");
            System.out.println("Commands:");
            System.out.println("  --help          Display this help message");
            System.out.println("  list-events     List all events (requires pre-loaded data)");
            System.out.println("Interactive mode is used if no arguments are provided.");
        } else if (args.length == 1 && args[0].equals("list-events")) {
            listEvents(events);
        } else {
            System.out.println("Unknown command. Use --help for usage information.");
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

    private static Integer readIntInput(String prompt, int min, int max) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                System.out.print(prompt);
                if (!SCANNER.hasNextLine()) {
                    return null;
                }
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
        return null;
    }

    private static String readNonEmptyString(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                return null;
            }
            String input = SCANNER.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
        return null;
    }

    private static LocalDateTime readDateTime(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                return null;
            }
            String input = SCANNER.nextLine().trim();
            try {
                return LocalDateTime.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm (e.g., 2025-04-15 14:30).");
            }
        }
        return null;
    }

    private static BigDecimal readPositiveBigDecimal(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                return null;
            }
            String input = SCANNER.nextLine().trim();
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) {
                    return value;
                }
                System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please enter a valid positive number (e.g., 10.50).");
            }
        }
        return null;
    }

    private static Event createEvent() {
        Integer type = readIntInput("Event Types: 1) Concert 2) Conference 3) Exhibition 4) Workshop\nSelect Event Type: ", 1, 4);
        if (type == null) {
            System.out.println("Invalid event type. Event creation cancelled.");
            return null;
        }

        String name = readNonEmptyString("Event Name: ");
        if (name == null) {
            System.out.println("Invalid event name. Event creation cancelled.");
            return null;
        }

        LocalDateTime start = readDateTime("Start Time (yyyy-MM-dd HH:mm): ");
        if (start == null) {
            System.out.println("Invalid start time. Event creation cancelled.");
            return null;
        }

        LocalDateTime end = readDateTime("End Time (yyyy-MM-dd HH:mm): ");
        if (end == null || end.isBefore(start)) {
            System.out.println("Invalid end time. Event creation cancelled.");
            return null;
        }

        String venue = readNonEmptyString("Venue Name: ");
        if (venue == null) {
            System.out.println("Invalid venue name. Event creation cancelled.");
            return null;
        }

        String address = readNonEmptyString("Venue Address: ");
        if (address == null) {
            System.out.println("Invalid venue address. Event creation cancelled.");
            return null;
        }

        Integer maxAttendees = readIntInput("Max Attendees: ", 1, Integer.MAX_VALUE);
        if (maxAttendees == null) {
            System.out.println("Invalid max attendees. Event creation cancelled.");
            return null;
        }

        return switch (type) {
            case 1 -> {
                String artist = readNonEmptyString("Artist: ");
                if (artist == null) {
                    System.out.println("Invalid artist. Event creation cancelled.");
                    yield null;
                }
                String genre = readNonEmptyString("Genre: ");
                if (genre == null) {
                    System.out.println("Invalid genre. Event creation cancelled.");
                    yield null;
                }
                BigDecimal price = readPositiveBigDecimal("Ticket Price (USD): ");
                if (price == null) {
                    System.out.println("Invalid ticket price. Event creation cancelled.");
                    yield null;
                }
                yield new Concert(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, artist, genre, new Money(price, USD));
            }
            case 2 -> {
                List<String> speakers = readNonEmptyList("Speakers (comma-separated names): ");
                if (speakers == null) {
                    System.out.println("Invalid speakers list. Event creation cancelled.");
                    yield null;
                }
                List<String> topics = readNonEmptyList("Topics (comma-separated): ");
                if (topics == null) {
                    System.out.println("Invalid topics list. Event creation cancelled.");
                    yield null;
                }
                BigDecimal fee = readPositiveBigDecimal("Registration Fee (USD): ");
                if (fee == null) {
                    System.out.println("Invalid registration fee. Event creation cancelled.");
                    yield null;
                }
                yield new Conference(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, speakers, topics, new Money(fee, USD));
            }
            case 3 -> {
                String theme = readNonEmptyString("Theme: ");
                if (theme == null) {
                    System.out.println("Invalid theme. Event creation cancelled.");
                    yield null;
                }
                List<String> exhibitors = readNonEmptyList("Exhibitors (comma-separated names): ");
                if (exhibitors == null) {
                    System.out.println("Invalid exhibitors list. Event creation cancelled.");
                    yield null;
                }
                BigDecimal fee = readPositiveBigDecimal("Entry Fee (USD): ");
                if (fee == null) {
                    System.out.println("Invalid entry fee. Event creation cancelled.");
                    yield null;
                }
                yield new Exhibition(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, theme, exhibitors, new Money(fee, USD));
            }
            case 4 -> {
                String instructor = readNonEmptyString("Instructor: ");
                if (instructor == null) {
                    System.out.println("Invalid instructor. Event creation cancelled.");
                    yield null;
                }
                String skillLevel = readNonEmptyString("Skill Level: ");
                if (skillLevel == null) {
                    System.out.println("Invalid skill level. Event creation cancelled.");
                    yield null;
                }
                BigDecimal fee = readPositiveBigDecimal("Participation Fee (USD): ");
                if (fee == null) {
                    System.out.println("Invalid participation fee. Event creation cancelled.");
                    yield null;
                }
                Integer maxParticipants = readIntInput("Max Participants: ", 1, Integer.MAX_VALUE);
                if (maxParticipants == null) {
                    System.out.println("Invalid max participants. Event creation cancelled.");
                    yield null;
                }
                yield new Workshop(EventId.generate(), name, start, end, new Location(venue, address),
                        maxAttendees, instructor, skillLevel, new Money(fee, USD), maxParticipants);
            }
            default -> null;
        };
    }

    private static List<String> readNonEmptyList(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                return null;
            }
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
        return null;
    }

    private static void registerAttendee(List<Event> events, List<Registration> registrations) {
        if (events.isEmpty()) {
            System.out.println("No events available to register for.");
            return;
        }

        System.out.println("Available Events:");
        for (int i = 0; i < events.size(); i++) {
            System.out.println(i + ": " + events.get(i).name() + " (" + eventType(events.get(i)) + ")");
        }
        Integer eventIdx = readIntInput("Select Event Index: ", 0, events.size() - 1);
        if (eventIdx == null) {
            System.out.println("Invalid event selection. Registration cancelled.");
            return;
        }

        String name = readNonEmptyString("Attendee Name: ");
        if (name == null) {
            System.out.println("Invalid attendee name. Registration cancelled.");
            return;
        }

        String email = readEmail("Email: ");
        if (email == null) {
            System.out.println("Invalid email. Registration cancelled.");
            return;
        }

        String phone = readNonEmptyString("Phone: ");
        if (phone == null) {
            System.out.println("Invalid phone. Registration cancelled.");
            return;
        }

        Attendee attendee = new Attendee(UUID.randomUUID(), name, email, phone);
        Event event = events.get(eventIdx);
        var regOpt = RegistrationOperations.registerForEvent(event, attendee, registrations);
        if (regOpt.isPresent()) {
            registrations.add(regOpt.get());
            System.out.println("Registration successful for " + event.name() + "!");
        } else {
            System.out.println("Registration failed: No capacity available for " + event.name() + ".");
        }
    }

    private static String readEmail(String prompt) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            System.out.print(prompt);
            if (!SCANNER.hasNextLine()) {
                return null;
            }
            String input = SCANNER.nextLine().trim();
            if (input.contains("@") && !input.isBlank()) {
                return input;
            }
            System.out.println("Please enter a valid email address containing '@'.");
        }
        return null;
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

        listEvents(events);
        Integer eventIdx = readIntInput("Select Event Index to View Info: ", 0, events.size() - 1);
        if (eventIdx == null) {
            System.out.println("Invalid event selection. Returning to main menu.");
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
        Integer eventIdx = readIntInput("Select Event Index to View Attendees: ", 0, events.size() - 1);
        if (eventIdx == null) {
            System.out.println("Invalid event selection. Returning to main menu.");
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
