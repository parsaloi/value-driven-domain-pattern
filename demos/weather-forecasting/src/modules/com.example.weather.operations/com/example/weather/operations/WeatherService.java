package com.example.weather.operations;

import com.example.weather.domain.*;
import com.example.core.common.utilities.Result;
import java.time.LocalDate;
import java.util.List;

public final class WeatherService {
    private final WeatherDataProvider dataProvider;
    
    public WeatherService(WeatherDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
    
    public Result<Forecast> getForecast(Location location, LocalDate date) {
        try { return Result.success(dataProvider.fetchForecast(location, date)); } 
        catch (ApiException e) { return Result.failure("API error: " + e.getMessage()); }
        catch (Exception e) { return Result.failure("Unexpected error: " + e.getMessage()); }
    }
    
    public Result<List<Forecast>> getForecastRange(Location location, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) 
            return Result.failure("Start date must be before or equal to end date");
        
        try { return Result.success(dataProvider.fetchForecastRange(location, startDate, endDate)); }
        catch (ApiException e) { return Result.failure("API error: " + e.getMessage()); }
        catch (Exception e) { return Result.failure("Unexpected error: " + e.getMessage()); }
    }
    
    public Result<List<WeatherAlert>> getAlerts(Location location, int days) {
        if (days <= 0 || days > 14) return Result.failure("Days must be between 1 and 14");
        
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(days - 1);
        
        return getForecastRange(location, startDate, endDate)
            .map(ForecastOperations::generateAlerts);
    }
}
