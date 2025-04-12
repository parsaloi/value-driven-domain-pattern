package com.example.eventmanagement.oop.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Workshop extends Event {
    private String instructor;
    private String skillLevel;
    private Money participationFee;
    private int maxParticipants;

    public Workshop(String name, LocalDateTime startTime, LocalDateTime endTime, Location location, int maxAttendees,
                    String instructor, String skillLevel, Money participationFee, int maxParticipants) {
        super(name, startTime, endTime, location, maxAttendees);
        this.instructor = instructor;
        this.skillLevel = skillLevel;
        this.participationFee = participationFee;
        this.maxParticipants = maxParticipants;
        validate();
    }

    @Override
    protected void validate() {
        super.validate();
        if (instructor == null || instructor.isBlank()) throw new IllegalArgumentException("Instructor cannot be null or blank");
        if (skillLevel == null || skillLevel.isBlank()) throw new IllegalArgumentException("Skill level cannot be null or blank");
        if (maxParticipants <= 0) throw new IllegalArgumentException("Max participants must be positive");
    }

    @Override
    public Money getFee() { return participationFee; }

    // Getters and setters
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public Money getParticipationFee() { return participationFee; }
    public void setParticipationFee(Money participationFee) { this.participationFee = participationFee; }
    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Workshop workshop)) return false;
        if (!super.equals(o)) return false;
        return maxParticipants == workshop.maxParticipants &&
                Objects.equals(instructor, workshop.instructor) &&
                Objects.equals(skillLevel, workshop.skillLevel) &&
                Objects.equals(participationFee, workshop.participationFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), instructor, skillLevel, participationFee, maxParticipants);
    }

    @Override
    public String toString() {
        return "Workshop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location=" + location +
                ", maxAttendees=" + maxAttendees +
                ", instructor='" + instructor + '\'' +
                ", skillLevel='" + skillLevel + '\'' +
                ", participationFee=" + participationFee +
                ", maxParticipants=" + maxParticipants +
                '}';
    }
}
