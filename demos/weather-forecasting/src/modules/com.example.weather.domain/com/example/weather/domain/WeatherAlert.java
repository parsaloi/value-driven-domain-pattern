package com.example.weather.domain;

import java.time.LocalDate;

public record WeatherAlert(LocalDate date, Location location, AlertType type, String message) {
    public WeatherAlert {
        if (date == null) throw new IllegalArgumentException("Date cannot be null");
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        if (type == null) throw new IllegalArgumentException("Alert type cannot be null");
        if (message == null || message.isBlank())
            throw new IllegalArgumentException("Alert message cannot be empty");
    }
}
