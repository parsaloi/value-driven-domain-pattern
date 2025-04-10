#!/bin/bash
set -e
echo "Running property-based tests..."
java --enable-preview \
    --module-path out \
    --module com.example.eventmanagement.tests/com.example.eventmanagement.tests.EventManagementDemoTest
echo "Property tests completed."
