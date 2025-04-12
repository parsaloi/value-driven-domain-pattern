@echo off
echo Running Event Management CLI (OOAD)...
java --enable-preview ^
    --module-path ..\..\out ^
    --module com.example.eventmanagement.oop/com.example.eventmanagement.oop.cli.EventManagementCli
echo CLI exited.
