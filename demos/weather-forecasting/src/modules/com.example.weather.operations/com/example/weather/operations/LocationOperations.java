package com.example.weather.operations;

import com.example.weather.domain.Location;

public final class LocationOperations {
    private LocationOperations() { }
    
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    public static double calculateDistanceBetween(Location location1, Location location2) {
        double lat1Rad = Math.toRadians(location1.latitude());
        double lat2Rad = Math.toRadians(location2.latitude());
        double deltaLat = Math.toRadians(location2.latitude() - location1.latitude());
        double deltaLon = Math.toRadians(location2.longitude() - location1.longitude());
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                  Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                  Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return EARTH_RADIUS_KM * c;
    }
}
