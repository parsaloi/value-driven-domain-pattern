package com.example.weather.domain;

import java.time.Duration;

public sealed interface WeatherEvent
    permits WeatherEvent.Rain, WeatherEvent.Snow, WeatherEvent.Clear,
            WeatherEvent.Cloudy, WeatherEvent.Thunderstorm {

    record Rain(double millimeters, Duration duration) implements WeatherEvent {
        public Rain {
            if (millimeters < 0) throw new IllegalArgumentException("Rainfall cannot be negative");
            if (duration == null) throw new IllegalArgumentException("Duration cannot be null");
        }
    }

    record Snow(double centimeters, Temperature temperature) implements WeatherEvent {
        public Snow {
            if (centimeters < 0) throw new IllegalArgumentException("Snowfall cannot be negative");
            if (temperature == null) throw new IllegalArgumentException("Temperature cannot be null");
            if (temperature.celsius() > 5) throw new IllegalArgumentException("Snow typically occurs below 5°C");
        }
    }

    record Clear(double visibilityKm) implements WeatherEvent {
        public Clear {
            if (visibilityKm <= 0) throw new IllegalArgumentException("Visibility must be positive");
        }

        public Clear() {
            this(Double.POSITIVE_INFINITY);
        }
    }

    record Cloudy(int coveragePercent) implements WeatherEvent {
        public Cloudy {
            if (coveragePercent < 0 || coveragePercent > 100)
                throw new IllegalArgumentException("Cloud coverage must be between 0 and 100 percent");
        }
    }

    record Thunderstorm(int severity, boolean hasHail) implements WeatherEvent {
        public Thunderstorm {
            if (severity < 1 || severity > 5)
                throw new IllegalArgumentException("Severity must be between 1 and 5");
        }
    }
}
