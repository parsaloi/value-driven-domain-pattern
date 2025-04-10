#!/bin/bash
set -e
echo "Running Event Management CLI..."
java --enable-preview \
    --module-path out \
    --module com.example.eventmanagement.cli/com.example.eventmanagement.cli.EventManagementCli
echo "CLI exited."
