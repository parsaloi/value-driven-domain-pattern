package com.example.weather.app;

import com.example.weather.domain.*;
import com.example.weather.operations.*;
import com.example.core.common.utilities.Result;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class WeatherSystemDemo {
    public static void main(String[] args) {
        Location sanFrancisco = new Location(37.7749, -122.4194, "San Francisco");
        WeatherDataProvider mockProvider = new MockWeatherProvider();
        WeatherService weatherService = new WeatherService(mockProvider);

        Result<List<Forecast>> forecastResult = weatherService.getForecastRange(
            sanFrancisco, LocalDate.now(), LocalDate.now().plusDays(6));

        switch (forecastResult) {
            case Result.Success<List<Forecast>> success -> {
                List<Forecast> forecasts = success.value();
                System.out.println("=== 7-Day Weather Forecast for " + sanFrancisco.name() + " ===\n");
                forecasts.forEach(WeatherSystemDemo::displayForecast);

                System.out.println("\n=== Weather Analysis ===");
                Optional<Forecast> hottestDay = ForecastOperations.findHottestDay(forecasts);
                Optional<Forecast> coldestDay = ForecastOperations.findColdestDay(forecasts);

                hottestDay.ifPresent(f -> System.out.printf(
                    "Hottest day: %s with high of %s\n", f.date(), f.high()));
                coldestDay.ifPresent(f -> System.out.printf(
                    "Coldest day: %s with low of %s\n", f.date(), f.low()));

                double totalRain = ForecastOperations.calculateTotalRainfall(forecasts);
                double totalSnow = ForecastOperations.calculateTotalSnowfall(forecasts);
                System.out.printf("Total rainfall: %.1f mm\n", totalRain);
                System.out.printf("Total snowfall: %.1f cm\n", totalSnow);

                Map<TemperatureCategory, List<Forecast>> categorized =
                    ForecastOperations.categorizeByTemperature(forecasts);
                System.out.println("\nTemperature categories:");
                categorized.forEach((category, categoryForecasts) ->
                    System.out.printf("  %s: %d days\n", category, categoryForecasts.size()));

                List<WeatherAlert> alerts = ForecastOperations.generateAlerts(forecasts);
                if (!alerts.isEmpty()) {
                    System.out.println("\n=== Weather Alerts ===");
                    alerts.forEach(alert ->
                        System.out.printf("%s: %s (%s)\n", alert.date(), alert.type(), alert.message()));
                }
            }
            case Result.Failure<List<Forecast>> failure ->
                System.err.println("Error getting forecast: " + failure.reason());
        }
    }

    private static void displayForecast(Forecast forecast) {
        System.out.printf("%s: %s to %s\n", forecast.date(), forecast.low(), forecast.high());
        System.out.println("Weather events:");
        forecast.events().forEach(event -> {
            String description = switch (event) {
                case WeatherEvent.Rain rain ->
                    String.format("Rain: %.1f mm over %d hours (%.1f mm/h)",
                        rain.millimeters(), rain.duration().toHours(),
                        WeatherEventOperations.calculateRainIntensity(rain));
                case WeatherEvent.Snow snow ->
                    String.format("Snow: %.1f cm at %s%s",
                        snow.centimeters(), snow.temperature(),
                        WeatherEventOperations.willSnowAccumulate(snow) ? " (will accumulate)" : "");
                case WeatherEvent.Clear clear ->
                    String.format("Clear: %.1f km visibility", clear.visibilityKm());
                case WeatherEvent.Cloudy cloudy ->
                    String.format("Cloudy: %d%% coverage", cloudy.coveragePercent());
                case WeatherEvent.Thunderstorm storm ->
                    String.format("Thunderstorm: severity %d%s",
                        storm.severity(), storm.hasHail() ? " with hail" : "");
            };
            System.out.println("  - " + description);
        });
        System.out.println();
    }
}

class MockWeatherProvider implements WeatherDataProvider {
    @Override
    public Forecast fetchForecast(Location location, LocalDate date) throws ApiException {
        return createSampleForecast(location, date);
    }

    @Override
    public List<Forecast> fetchForecastRange(Location location, LocalDate startDate, LocalDate endDate)
            throws ApiException {
        List<Forecast> forecasts = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            forecasts.add(createSampleForecast(location, current));
            current = current.plusDays(1);
        }
        return List.copyOf(forecasts);
    }

    private Forecast createSampleForecast(Location location, LocalDate date) {
        int dayOffset = date.getDayOfYear();
        Temperature high = new Temperature(15 + (dayOffset % 10));
        Temperature low = new Temperature(5 + (dayOffset % 10));
        List<WeatherEvent> events = new ArrayList<>();

        switch (dayOffset % 5) {
            case 0 -> events.add(new WeatherEvent.Rain(10.0, Duration.ofHours(2)));
            case 1 -> events.add(new WeatherEvent.Snow(5.0, new Temperature(-2)));
            case 2 -> events.add(new WeatherEvent.Cloudy(75));
            case 3 -> events.add(new WeatherEvent.Thunderstorm(3, false));
            case 4 -> events.add(new WeatherEvent.Clear());
        }

        return new Forecast(date, location, high, low, events);
    }
}
