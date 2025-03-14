#!/bin/bash
# scripts/test.sh
set -e
echo "Running demo as test..."
java --enable-preview \
    --module-path out \
    --module com.example.eventmanagement.app/com.example.eventmanagement.app.EventManagementDemo
echo "Demo test passed."
