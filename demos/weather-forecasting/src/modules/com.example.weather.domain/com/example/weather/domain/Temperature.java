package com.example.weather.domain;

public record Temperature(double celsius) {
    private static final double ABSOLUTE_ZERO = -273.15;

    public Temperature {
        if (celsius < ABSOLUTE_ZERO)
            throw new IllegalArgumentException("Temperature cannot be below absolute zero (" + ABSOLUTE_ZERO + "°C)");
    }

    @Override
    public String toString() { return String.format("%.1f°C", celsius); }
}
