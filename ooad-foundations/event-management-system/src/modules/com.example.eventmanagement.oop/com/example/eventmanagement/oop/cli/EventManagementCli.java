package com.example.eventmanagement.oop.cli;

import com.example.eventmanagement.oop.domain.*;
import com.example.eventmanagement.oop.operations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class EventManagementCli {
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final RegistrationOperations REG_OPS =
            new RegistrationOperations(new RevenueStrategy.DefaultRevenueStrategy());
    private static final CapacityObserver CAPACITY_OBSERVER = new CapacityObserver.ConsoleCapacityObserver();

    public static void main(String[] args) {
        List<Event> events = new ArrayList<>();
        List<Registration> registrations = new ArrayList<>();

        System.out.println("Welcome to Event Organizer CLI (OOP)");
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

        Location location = new Location(venue, address);
        return switch (type) {
            case 1 -> {
                System.out.print("Artist: ");
                String artist = SCANNER.nextLine();
                System.out.print("Genre: ");
                String genre = SCANNER.nextLine();
                System.out.print("Ticket Price (USD): ");
                BigDecimal price = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                yield EventFactory.createConcert(name, start, end, location, maxAttendees, artist, genre, price);
            }
            case 2 -> {
                System.out.print("Speakers (comma-separated names): ");
                List<String> speakers = Arrays.asList(SCANNER.nextLine().split(",\\s*"));
                System.out.print("Topics (comma-separated): ");
                List<String> topics = Arrays.asList(SCANNER.nextLine().split(",\\s*"));
                System.out.print("Registration Fee (USD): ");
                BigDecimal fee = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                yield EventFactory.createConference(name, start, end, location, maxAttendees, speakers, topics, fee);
            }
            case 3 -> {
                System.out.print("Theme: ");
                String theme = SCANNER.nextLine();
                System.out.print("Exhibitors (comma-separated names): ");
                List<String> exhibitors = Arrays.asList(SCANNER.nextLine().split(",\\s*"));
                System.out.print("Entry Fee (USD): ");
                BigDecimal fee = SCANNER.nextBigDecimal();
                SCANNER.nextLine();
                yield EventFactory.createExhibition(name, start, end, location, maxAttendees, theme, exhibitors, fee);
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
                yield EventFactory.createWorkshop(name, start, end, location, maxAttendees, instructor, skillLevel, fee, maxParticipants);
            }
            default -> throw new IllegalArgumentException("Invalid event type");
        };
    }

    private static void registerAttendee(List<Event> events, List<Registration> registrations) {
        if (events.isEmpty()) {
            System.out.println("No events available.");
            return;
        }
        listEvents(events);
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

        Attendee attendee = new Attendee(name, email, phone);
        Event event = events.get(eventIdx);
        try {
            REG_OPS.registerForEvent(event, attendee, registrations, CAPACITY_OBSERVER);
            System.out.println("Registration successful!");
        } catch (IllegalStateException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void viewRevenue(List<Registration> registrations) {
        Money revenue = REG_OPS.calculateTotalRevenue(registrations);
        System.out.println("Total Revenue: " + revenue.getAmount() + " " + revenue.getCurrency());
    }

    private static void listEvents(List<Event> events) {
        if (events.isEmpty()) {
            System.out.println("No events.");
            return;
        }
        for (int i = 0; i < events.size(); i++) {
            Event e = events.get(i);
            System.out.println(i + ": " + e.getName() + " (" + e.getStartTime().format(DATE_FORMAT) +
                    " - " + e.getEndTime().format(DATE_FORMAT) + ") - " + eventType(e));
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
        System.out.println("Name: " + event.getName());
        System.out.println("Start Time: " + event.getStartTime().format(DATE_FORMAT));
        System.out.println("End Time: " + event.getEndTime().format(DATE_FORMAT));
        System.out.println("Location: " + event.getLocation().getName() + " (" + event.getLocation().getAddress() + ")");
        System.out.println("Max Attendees: " + event.getMaxAttendees());

        if (event instanceof Concert concert) {
            System.out.println("Artist: " + concert.getArtist());
            System.out.println("Genre: " + concert.getGenre());
            System.out.println("Ticket Price: " + concert.getTicketPrice().getAmount() + " " + concert.getTicketPrice().getCurrency());
        } else if (event instanceof Conference conference) {
            System.out.println("Speakers: " + String.join(", ", conference.getSpeakers()));
            System.out.println("Topics: " + String.join(", ", conference.getTopics()));
            System.out.println("Registration Fee: " + conference.getRegistrationFee().getAmount() + " " + conference.getRegistrationFee().getCurrency());
        } else if (event instanceof Exhibition exhibition) {
            System.out.println("Theme: " + exhibition.getTheme());
            System.out.println("Exhibitors: " + String.join(", ", exhibition.getExhibitors()));
            System.out.println("Entry Fee: " + exhibition.getEntryFee().getAmount() + " " + exhibition.getEntryFee().getCurrency());
        } else if (event instanceof Workshop workshop) {
            System.out.println("Instructor: " + workshop.getInstructor());
            System.out.println("Skill Level: " + workshop.getSkillLevel());
            System.out.println("Participation Fee: " + workshop.getParticipationFee().getAmount() + " " + workshop.getParticipationFee().getCurrency());
            System.out.println("Max Participants: " + workshop.getMaxParticipants());
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
                .filter(reg -> reg.getEvent().getId().equals(event.getId()))
                .toList();
        if (eventRegistrations.isEmpty()) {
            System.out.println("No attendees registered for " + event.getName() + ".");
            return;
        }

        System.out.println("\nAttendees for " + event.getName() + ":");
        for (Registration reg : eventRegistrations) {
            Attendee attendee = reg.getAttendee();
            System.out.println("Name: " + attendee.getName() + ", Email: " + attendee.getEmail() + ", Phone: " + attendee.getPhone());
        }
    }

    private static String eventType(Event event) {
        if (event instanceof Concert) return "Concert";
        if (event instanceof Conference) return "Conference";
        if (event instanceof Exhibition) return "Exhibition";
        if (event instanceof Workshop) return "Workshop";
        return "Unknown";
    }
}
