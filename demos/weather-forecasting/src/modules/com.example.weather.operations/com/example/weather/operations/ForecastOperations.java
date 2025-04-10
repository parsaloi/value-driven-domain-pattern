package com.example.weather.operations;

import com.example.weather.domain.*;
import java.util.*;
import java.util.stream.Collectors;

public final class ForecastOperations {
    private ForecastOperations() { }

    public static List<Forecast> filterByTemperature(
            List<Forecast> forecasts, Temperature minimum, Temperature maximum) {
        return forecasts.stream()
            .filter(f -> f.high().celsius() <= maximum.celsius() &&
                         f.low().celsius() >= minimum.celsius())
            .collect(Collectors.toUnmodifiableList());
    }

    public static Optional<Forecast> findHottestDay(List<Forecast> forecasts) {
        return forecasts.stream().max(Comparator.comparing(f -> f.high().celsius()));
    }

    public static Optional<Forecast> findColdestDay(List<Forecast> forecasts) {
        return forecasts.stream().min(Comparator.comparing(f -> f.low().celsius()));
    }

    public static boolean hasPrecipitation(List<Forecast> forecasts) {
        return forecasts.stream()
            .anyMatch(f -> f.events().stream()
                .anyMatch(e -> e instanceof WeatherEvent.Rain || e instanceof WeatherEvent.Snow));
    }

    public static double calculateTotalRainfall(List<Forecast> forecasts) {
        return forecasts.stream()
            .flatMap(f -> f.events().stream())
            .filter(e -> e instanceof WeatherEvent.Rain)
            .mapToDouble(e -> ((WeatherEvent.Rain) e).millimeters())
            .sum();
    }

    public static double calculateTotalSnowfall(List<Forecast> forecasts) {
        return forecasts.stream()
            .flatMap(f -> f.events().stream())
            .filter(e -> e instanceof WeatherEvent.Snow)
            .mapToDouble(e -> ((WeatherEvent.Snow) e).centimeters())
            .sum();
    }

    public static Map<TemperatureCategory, List<Forecast>> categorizeByTemperature(
            List<Forecast> forecasts) {
        return forecasts.stream()
            .collect(Collectors.groupingBy(
                f -> {
                    double avgTemp = (f.high().celsius() + f.low().celsius()) / 2;
                    if (avgTemp < 0) return TemperatureCategory.FREEZING;
                    if (avgTemp < 10) return TemperatureCategory.COLD;
                    if (avgTemp < 20) return TemperatureCategory.MILD;
                    if (avgTemp < 30) return TemperatureCategory.WARM;
                    return TemperatureCategory.HOT;
                },
                Collectors.toUnmodifiableList()
            ));
    }

    public static Temperature calculateAverageTemperature(Forecast forecast) {
        return new Temperature((forecast.high().celsius() + forecast.low().celsius()) / 2);
    }

    public static List<WeatherAlert> generateAlerts(List<Forecast> forecasts) {
        List<WeatherAlert> alerts = new ArrayList<>();
        for (Forecast forecast : forecasts) {
            if (forecast.high().celsius() > 35) {
                alerts.add(new WeatherAlert(
                    forecast.date(), forecast.location(), AlertType.EXTREME_HEAT,
                    "Extreme heat warning: " + forecast.high()));
            }
            if (forecast.low().celsius() < -15) {
                alerts.add(new WeatherAlert(
                    forecast.date(), forecast.location(), AlertType.EXTREME_COLD,
                    "Extreme cold warning: " + forecast.low()));
            }
            for (WeatherEvent event : forecast.events()) {
                switch (event) {
                    case WeatherEvent.Thunderstorm t when t.severity() >= 4 ->
                        alerts.add(new WeatherAlert(
                            forecast.date(), forecast.location(), AlertType.SEVERE_THUNDERSTORM,
                            "Severe thunderstorm warning, level " + t.severity()));
                    case WeatherEvent.Rain r when WeatherEventOperations.calculateRainIntensity(r) > 20.0 ->
                        alerts.add(new WeatherAlert(
                            forecast.date(), forecast.location(), AlertType.HEAVY_RAINFALL,
                            "Heavy rainfall warning: " + r.millimeters() + "mm"));
                    case WeatherEvent.Snow s when s.centimeters() > 25.0 ->
                        alerts.add(new WeatherAlert(
                            forecast.date(), forecast.location(), AlertType.HEAVY_SNOWFALL,
                            "Heavy snowfall warning: " + s.centimeters() + "cm"));
                    default -> { /* No alert needed */ }
                }
            }
        }
        return List.copyOf(alerts);
    }
}
