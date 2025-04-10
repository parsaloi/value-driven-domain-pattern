package com.example.weather.operations;

import com.example.weather.domain.*;
import java.time.LocalDate;
import java.util.List;

public interface WeatherDataProvider {
    Forecast fetchForecast(Location location, LocalDate date) throws ApiException;
    List<Forecast> fetchForecastRange(Location location, LocalDate startDate, LocalDate endDate) 
        throws ApiException;
}
