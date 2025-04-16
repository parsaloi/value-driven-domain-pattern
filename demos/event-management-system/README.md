# Value-Driven Event Management System

This demo showcases the Value-Driven Domain Pattern, a modern approach to Domain-Driven Design in Java. It uses immutable records, sealed interfaces, pattern matching, and pure functions to model an event management system, contrasting with traditional OOAD.

## Structure
- **core/domain**: Domain model (records, sealed interfaces).
- **core/operations/**: Stateless operations.
- **core/common**: Common utilities
- **cli-app/**: Command-line interface.

## Features
- Create events (Concert, Conference, Exhibition, Workshop)
- Register attendees with capacity checks
- View event details, attendees, and revenue
- Type-safe, immutable, and thread-safe design

## Testing Locally

It is recommended you run the `cli-app` using a standalone executable JAR (Fatjar) as demonstrated below:

1. First ensure required artifacts are published in your local maven repository

* Unix-like Systems(Linux/MacOs):

```bash
cd core/
./gradlew publishToMavenLocal --no-daemon

# The artifacts (common, domain, operations) are published to ~/.m2/repository/com/example/core/
```

* Windows
```cmd
cd core
gradlew.bat publishToMavenLocal --no-daemon
# The artifacts (common, domain, operations) are published to %USERPROFILE%\.m2\repository\com\example\core\
```

2. Build a Fatjar of the `cli-app`

* Unix-like Systems(Linux/MacOs):
```bash
cd ..
cd cli-app
./gradlew clean build --no-daemon
```

* Windows
```cmd
cd ..
cd cli-app
gradlew.bat clean build --no-daemon
```

3. Test the standalone `cli-app`

* Unix-like Systems(Linux/MacOs):
```bash
java -jar build/libs/cli-app-1.0.0.jar --help

# Expected sample output
Event Organizer CLI:
Usage: java -jar cli-app.jar [command]
Commands:
  --help          Display this help message
  list-events     List all events (requires pre-loaded data)
Interactive mode is used if no arguments are provided.
```

* Windows
```cmd
java -jar build\libs\cli-app-1.0.0.jar --help

# Expected sample output
Event Organizer CLI: Non-Interactive Mode
Usage: java -jar cli-app.jar [command]
Commands:
  --help          Display this help message
  list-events     List all events (requires pre-loaded data)
Interactive mode is used if no arguments are provided.
```

## IntelliJ Setup:  

1. Open value-driven-domain-pattern/demo/event-management-system in IntelliJ.

2. Import as Gradle project (File > New > Project from Existing Sources > select build.gradle.kts).

3. Verify modules: core:common, core:domain, core:operations, cli-app.

4. Run CLI via Gradle: cli-app > Tasks > application > run. or  
open `cli-app/src/main/java/com/example/eventmanagement/cli/EventManagementCli.java` and run the `main()` method

## Requirements
- Java 21+
- Maven (Required)
- Gradle (Optional)
