package com.example.eventmanagement.oop.domain;

import java.util.Objects;

public class Registration {
    private Event event;
    private Attendee attendee;
    private RegistrationStatus status;

    public Registration(Event event, Attendee attendee, RegistrationStatus status) {
        this.event = event;
        this.attendee = attendee;
        this.status = status;
    }

    // Getters and setters
    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
    public Attendee getAttendee() { return attendee; }
    public void setAttendee(Attendee attendee) { this.attendee = attendee; }
    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Registration that)) return false;
        return Objects.equals(event, that.event) &&
                Objects.equals(attendee, that.attendee) &&
                status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, attendee, status);
    }

    @Override
    public String toString() {
        return "Registration{" +
                "event=" + event +
                ", attendee=" + attendee +
                ", status=" + status +
                '}';
    }
}
