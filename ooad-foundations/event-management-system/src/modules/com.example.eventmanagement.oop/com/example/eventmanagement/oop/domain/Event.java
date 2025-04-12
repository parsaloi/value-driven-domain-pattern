package com.example.eventmanagement.oop.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public abstract class Event {
    protected UUID id;
    protected String name;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected Location location;
    protected int maxAttendees;

    public Event(String name, LocalDateTime startTime, LocalDateTime endTime, Location location, int maxAttendees) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.maxAttendees = maxAttendees;
        validate();
    }

    protected void validate() {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Name cannot be null or blank");
        if (startTime == null || endTime == null || endTime.isBefore(startTime)) throw new IllegalArgumentException("Invalid time range");
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        if (maxAttendees <= 0) throw new IllegalArgumentException("Max attendees must be positive");
    }

    public abstract Money getFee();

    // Getters and setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }
    public int getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Event event)) return false;
        return maxAttendees == event.maxAttendees &&
                Objects.equals(id, event.id) &&
                Objects.equals(name, event.name) &&
                Objects.equals(startTime, event.startTime) &&
                Objects.equals(endTime, event.endTime) &&
                Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, startTime, endTime, location, maxAttendees);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location=" + location +
                ", maxAttendees=" + maxAttendees +
                '}';
    }
}
