package com.example.weather.domain;

import java.time.LocalDate;
import java.util.List;

public record Forecast(
    LocalDate date,
    Location location,
    Temperature high,
    Temperature low,
    List<WeatherEvent> events
) {
    public Forecast {
        if (date == null) throw new IllegalArgumentException("Date cannot be null");
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        if (high == null) throw new IllegalArgumentException("High temperature cannot be null");
        if (low == null) throw new IllegalArgumentException("Low temperature cannot be null");
        if (high.celsius() < low.celsius())
            throw new IllegalArgumentException("High temperature cannot be lower than low temperature");
        events = List.copyOf(events != null ? events : List.of());
    }
}
