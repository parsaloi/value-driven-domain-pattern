#!/bin/bash
# scripts/test.sh
set -e
echo "Running demo as test..."
java --enable-preview \
    --module-path out \
    --module com.example.weather.app/com.example.weather.app.WeatherSystemDemo
echo "Demo test passed."
