package com.example.eventmanagement.oop.operations;

import com.example.eventmanagement.oop.domain.*;

import java.util.List;

public class RegistrationOperations {
    private final RevenueStrategy revenueStrategy;

    public RegistrationOperations(RevenueStrategy revenueStrategy) {
        this.revenueStrategy = revenueStrategy;
    }

    public Registration registerForEvent(Event event, Attendee attendee, List<Registration> existingRegistrations,
                                         CapacityObserver observer) {
        if (!EventOperations.hasAvailableCapacity(event, existingRegistrations.size())) {
            throw new IllegalStateException("No capacity available");
        }
        Registration reg = new Registration(event, attendee, RegistrationStatus.CONFIRMED);
        existingRegistrations.add(reg);
        observer.updateCapacity(event, existingRegistrations.size());
        return reg;
    }

    public Money calculateTotalRevenue(List<Registration> registrations) {
        return revenueStrategy.calculateRevenue(registrations);
    }
}
