package com.example.eventmanagement.oop.operations;

import com.example.eventmanagement.oop.domain.Event;

public interface CapacityObserver {
    void updateCapacity(Event event, int currentRegistrations);

    class ConsoleCapacityObserver implements CapacityObserver {
        @Override
        public void updateCapacity(Event event, int currentRegistrations) {
            System.out.println("Capacity update for " + event.getName() + ": " + currentRegistrations + "/" + event.getMaxAttendees());
        }
    }
}
