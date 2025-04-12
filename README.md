# Value Driven Domain Pattern

This project showcases two implementations of an event management system to compare classic Object-Oriented Analysis and Design (OOAD) with the Value-Driven Domain Pattern (VDD). The demos are presented as part of a conference talk, demonstrating modern Java design practices.

- **OOAD Demo**: Uses mutable classes, inheritance, and Gang of Four patterns (Factory, Strategy, Observer).
- **VDD Demo**: Leverages immutable records, sealed interfaces, pattern matching, and pure functions.

Both demos include a command-line interface (CLI) to create events, register attendees, view details, and calculate revenue.

## Prerequisites

- **Java 21**: Ensure JDK 21 is installed. Download from [Adoptium](https://adoptium.net/) or [OpenJDK]
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
Each demo has scripts to build and run the CLI. Use .sh scripts on Unix-like systems or .bat scripts on Windows.

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


### VDD Demo
* Unix-like Systems:
Build:
```bash

cd demos/event-management-system
./src/scripts/build.sh

# Creates out/ with compiled classes.
```
Run CLI:
```bash

./src/scripts/run-cli.sh

#Launches the CLI with similar functionality to OOAD.
```
* Windows:
Build:
```cmd

cd demos\event-management-system
src\windows-scripts\build.bat
```
Run CLI:
```cmd

src\windows-scripts\run-vdd-cli.bat
```

## CLI Usage
Both CLIs offer:  
* Create events (Concert, Conference, Exhibition, Workshop)

* Register attendees with capacity checks

* View event details, attendees, and revenue

* List events or exit

```
#Example Interaction (OOAD CLI):

Welcome to Event Organizer CLI (OOP)
Options: 1) Create Event 2) Register Attendee 3) View Revenue 4) List Events 5) Exit 6) View Event Info 7) View Event Attendees
Choose an option: 1
Event Types: 1) Concert 2) Conference 3) Exhibition 4) Workshop
Select Event Type: 2
Event Name: Kenya JUG Sessions
Start Time (yyyy-MM-dd HH:mm): 2025-04-12 20:00
End Time (yyyy-MM-dd HH:mm): 2025-04-12 21:00
```

The VDD CLI follows a similar flow with immutable data handling.

## Additional Notes

No External Dependencies: Just Pure JDK 21

## Troubleshooting:
Ensure `JAVA_HOME` is set to JDK 21.

Check script permissions (chmod +x *.sh on Unix-like systems).

For Windows, run .bat scripts in Command Prompt.

## Contact
For issues or questions, reach out to the presenter or open an issue in the repository at https://gitlab.com/parsaizme/value-driven-domain-pattern.
Happy coding, and enjoy the demos!


