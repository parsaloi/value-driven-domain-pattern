package com.example.eventmanagement.operations;

import com.example.eventmanagement.domain.Attendee;
import com.example.eventmanagement.domain.Event;
import com.example.eventmanagement.domain.Money;
import com.example.eventmanagement.domain.Registration;
import com.example.eventmanagement.domain.RegistrationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class RegistrationOperations {
    private RegistrationOperations() {}

    public static Registration createRegistration(Event event, Attendee attendee, RegistrationStatus status) {
        return new Registration(UUID.randomUUID(), event, attendee, LocalDateTime.now(), status);
    }

    public static Registration updateStatus(Registration registration, RegistrationStatus newStatus) {
        return new Registration(registration.id(), registration.event(), registration.attendee(),
                registration.registrationTime(), newStatus);
    }

    public static Money calculateTotalRevenue(List<Registration> registrations) {
        List<Money> fees = registrations.stream()
                .filter(r -> r.status() == RegistrationStatus.CONFIRMED || r.status() == RegistrationStatus.ATTENDED)
                .map(r -> EventOperations.getEventFee(r.event()))
                .toList();
        return MoneyOperations.sum(fees);
    }

    public static Optional<Registration> registerForEvent(Event event, Attendee attendee, List<Registration> existingRegistrations) {
        if (!EventOperations.hasAvailableCapacity(event, existingRegistrations.size())) {
            return Optional.empty();
        }
        return Optional.of(createRegistration(event, attendee, RegistrationStatus.CONFIRMED));
    }
}
