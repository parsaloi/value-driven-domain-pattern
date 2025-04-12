package com.example.eventmanagement.oop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Conference extends Event {
    private List<String> speakers;
    private List<String> topics;
    private Money registrationFee;

    public Conference(String name, LocalDateTime startTime, LocalDateTime endTime, Location location, int maxAttendees,
                      List<String> speakers, List<String> topics, Money registrationFee) {
        super(name, startTime, endTime, location, maxAttendees);
        this.speakers = new ArrayList<>(speakers);
        this.topics = new ArrayList<>(topics);
        this.registrationFee = registrationFee;
        validate();
    }

    @Override
    protected void validate() {
        super.validate();
        if (speakers == null || speakers.isEmpty()) throw new IllegalArgumentException("Speakers list cannot be null or empty");
        if (topics == null || topics.isEmpty()) throw new IllegalArgumentException("Topics list cannot be null or empty");
    }

    @Override
    public Money getFee() { return registrationFee; }

    // Getters and setters
    public List<String> getSpeakers() { return new ArrayList<>(speakers); }
    public void setSpeakers(List<String> speakers) { this.speakers = new ArrayList<>(speakers); }
    public List<String> getTopics() { return new ArrayList<>(topics); }
    public void setTopics(List<String> topics) { this.topics = new ArrayList<>(topics); }
    public Money getRegistrationFee() { return registrationFee; }
    public void setRegistrationFee(Money registrationFee) { this.registrationFee = registrationFee; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Conference conference)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(speakers, conference.speakers) &&
                Objects.equals(topics, conference.topics) &&
                Objects.equals(registrationFee, conference.registrationFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), speakers, topics, registrationFee);
    }

    @Override
    public String toString() {
        return "Conference{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location=" + location +
                ", maxAttendees=" + maxAttendees +
                ", speakers=" + speakers +
                ", topics=" + topics +
                ", registrationFee=" + registrationFee +
                '}';
    }
}
