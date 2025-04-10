package com.example.weather.operations;

import com.example.weather.domain.*;

public final class WeatherEventOperations {
    private WeatherEventOperations() { } // Non-instantiable

    public static double calculateRainIntensity(WeatherEvent.Rain rain) {
        double hours = rain.duration().toSeconds() / 3600.0;
        return hours > 0 ? rain.millimeters() / hours : 0;
    }

    public static boolean willSnowAccumulate(WeatherEvent.Snow snow) {
        return snow.temperature().celsius() <= 0;
    }
}
