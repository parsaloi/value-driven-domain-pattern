package com.example.eventmanagement.oop.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Exhibition extends Event {
    private String theme;
    private List<String> exhibitors;
    private Money entryFee;

    public Exhibition(String name, LocalDateTime startTime, LocalDateTime endTime, Location location, int maxAttendees,
                      String theme, List<String> exhibitors, Money entryFee) {
        super(name, startTime, endTime, location, maxAttendees);
        this.theme = theme;
        this.exhibitors = new ArrayList<>(exhibitors);
        this.entryFee = entryFee;
        validate();
    }

    @Override
    protected void validate() {
        super.validate();
        if (theme == null || theme.isBlank()) throw new IllegalArgumentException("Theme cannot be null or blank");
        if (exhibitors == null || exhibitors.isEmpty()) throw new IllegalArgumentException("Exhibitors list cannot be null or empty");
    }

    @Override
    public Money getFee() { return entryFee; }

    // Getters and setters
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }
    public List<String> getExhibitors() { return new ArrayList<>(exhibitors); }
    public void setExhibitors(List<String> exhibitors) { this.exhibitors = new ArrayList<>(exhibitors); }
    public Money getEntryFee() { return entryFee; }
    public void setEntryFee(Money entryFee) { this.entryFee = entryFee; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Exhibition exhibition)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(theme, exhibition.theme) &&
                Objects.equals(exhibitors, exhibition.exhibitors) &&
                Objects.equals(entryFee, exhibition.entryFee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), theme, exhibitors, entryFee);
    }

    @Override
    public String toString() {
        return "Exhibition{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location=" + location +
                ", maxAttendees=" + maxAttendees +
                ", theme='" + theme + '\'' +
                ", exhibitors=" + exhibitors +
                ", entryFee=" + entryFee +
                '}';
    }
}
