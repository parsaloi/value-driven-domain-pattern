package com.example.weather.domain;

public record Location(double latitude, double longitude, String name) {
    public Location {
        if (latitude < -90 || latitude > 90)
            throw new IllegalArgumentException("Latitude must be between -90 and 90 degrees");
        if (longitude < -180 || longitude > 180)
            throw new IllegalArgumentException("Longitude must be between -180 and 180 degrees");
    }

    @Override
    public String toString() {
        return name != null && !name.isBlank()
            ? name + " (" + latitude + ", " + longitude + ")"
            : latitude + ", " + longitude;
    }
}
