# Value-Driven Event Management System

This demo showcases the Value-Driven Domain Pattern, a modern approach to Domain-Driven Design in Java. It uses immutable records, sealed interfaces, pattern matching, and pure functions to model an event management system, contrasting with traditional OOAD.

## Structure
- **src/modules/com.example.eventmanagement.domain/**: Domain model (records, sealed interfaces).
- **src/modules/com.example.eventmanagement.operations/**: Stateless operations.
- **src/modules/com.example.eventmanagement.cli/**: Command-line interface.
- **src/scripts/**: Build and run scripts.
- **out/**: Compiled output.

## Features
- Create events (Concert, Conference, Exhibition, Workshop)
- Register attendees with capacity checks
- View event details, attendees, and revenue
- Type-safe, immutable, and thread-safe design

## Running
1. Build: `./src/scripts/build.sh`
2. Run CLI: `./src/scripts/run-cli.sh`

## Requirements
- Java 21+
- No external dependencies

## Comparison
See `../ooad-foundations/event-management-system/` for the traditional OOAD version.


