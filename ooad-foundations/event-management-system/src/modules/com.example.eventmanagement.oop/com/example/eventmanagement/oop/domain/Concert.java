package com.example.eventmanagement.oop.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Concert extends Event {
    private String artist;
    private String genre;
    private Money ticketPrice;

    public Concert(String name, LocalDateTime startTime, LocalDateTime endTime, Location location, int maxAttendees,
                   String artist, String genre, Money ticketPrice) {
        super(name, startTime, endTime, location, maxAttendees);
        this.artist = artist;
        this.genre = genre;
        this.ticketPrice = ticketPrice;
        validate();
    }

    @Override
    protected void validate() {
        super.validate();
        if (artist == null || artist.isBlank()) throw new IllegalArgumentException("Artist cannot be null or blank");
    }

    @Override
    public Money getFee() { return ticketPrice; }

    // Getters and setters
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Money getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(Money ticketPrice) { this.ticketPrice = ticketPrice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Concert concert)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(artist, concert.artist) &&
                Objects.equals(genre, concert.genre) &&
                Objects.equals(ticketPrice, concert.ticketPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), artist, genre, ticketPrice);
    }

    @Override
    public String toString() {
        return "Concert{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", location=" + location +
                ", maxAttendees=" + maxAttendees +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", ticketPrice=" + ticketPrice +
                '}';
    }
}
