# Value Driven Domain Pattern

This project showcases two implementations of an event management system to compare classic Object-Oriented Analysis and Design (OOAD) with the Value-Driven Domain Pattern (VDD). The demos are presented as part of a conference talk, demonstrating modern Java design practices.

- **OOAD Demo**: Uses mutable classes, inheritance, and Gang of Four patterns (Factory, Strategy, Observer).
- **VDD Demo**: Leverages immutable records, sealed interfaces, pattern matching, and pure functions.

Both demos include a command-line interface (CLI) to create events, register attendees, view details, and calculate revenue.

## Prerequisites

- **Java 21**: Ensure JDK 21 is installed. Download from [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/#java21)
- **Operating System**: Unix-like (Linux, macOS) or Windows.
- **Shell**: Bash for `.sh` scripts (Unix-like) or Command Prompt for `.bat` scripts (Windows).
- **Git**: To clone the repository (optional).

Verify Java version:
```bash
java --version

# Expected sample output:
openjdk 21.0.6 2025-01-21 LTS
OpenJDK Runtime Environment (build 21.0.6+10-LTS)
OpenJDK 64-Bit Server VM (build 21.0.6+10-LTS, mixed mode, sharing)
```


## Setup
Clone or Download:  

* Clone the repository:
```bash
git clone <repository-url>
cd value-driven-domain-pattern
```

* Or download and extract the project zip.


Directory Navigation:  

* VDD demo: demos/event-management-system/

* OOAD demo: ooad-foundations/event-management-system/

## Running the Demos

### VDD Demo

[![asciicast](https://asciinema.org/a/Y0HXrJEQAMtRf0bmdHQfwrhfx.svg)](https://asciinema.org/a/Y0HXrJEQAMtRf0bmdHQfwrhfx)

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
Event Organizer CLI:
Usage: java -jar cli-app.jar [command]
Commands:
  --help          Display this help message
  list-events     List all events (requires pre-loaded data)
Interactive mode is used if no arguments are provided.
```

### OOAD Demo
* Unix-like Systems:
Build:
```bash

cd ooad-foundations/event-management-system
./src/scripts/build.sh

# Creates out/ with compiled classes.
```
Run CLI:

```bash

./src/scripts/run-cli.sh

# Launches the CLI with options to create events, register attendees, etc.
```

* Windows:
Build:  
```cmd

cd ooad-foundations\event-management-system
src\windows-scripts\build.bat
```
Run CLI:  
```cmd

src\windows-scripts\run-ooad-cli.bat
```

## CLI Usage
Both CLIs offer:  
* Create events (Concert, Conference, Exhibition, Workshop)

* Register attendees with capacity checks

* View event details, attendees, and revenue

* List events or exit

## Requirements
- Java 21+
- Maven (Required)
- Gradle (Optional)

## Troubleshooting:
Ensure `JAVA_HOME` is set to JDK 21.

Check script permissions (chmod +x *.sh on Unix-like systems).

For Windows, run .bat scripts in Command Prompt.

## Contact
For issues or questions, reach out to the presenter or open an issue in the repository at https://gitlab.com/parsaizme/value-driven-domain-pattern.
Happy coding, and enjoy the demos!


