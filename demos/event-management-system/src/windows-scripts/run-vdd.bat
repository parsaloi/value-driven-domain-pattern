@echo off
echo Running Event Management CLI (VDD)...
java --enable-preview ^
    --module-path ..\..\out ^
    --module com.example.eventmanagement.cli/com.example.eventmanagement.cli.EventManagementCli
echo CLI exited.
