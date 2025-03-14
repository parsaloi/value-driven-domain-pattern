package com.example.eventmanagement.app;

import com.example.core.common.utilities.completion.OperationResult;
import com.example.core.common.utilities.completion.TaskCompletionHandler;
import com.example.core.common.utilities.operations.OperationContext;
import com.example.core.common.utilities.operations.OperationsTaskScope;
import com.example.eventmanagement.domain.*;
import com.example.eventmanagement.operations.EventOperations;
import com.example.eventmanagement.operations.MoneyOperations;
import com.example.eventmanagement.operations.RegistrationOperations;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class EventManagementDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        EventId concertId = EventId.generate();
        Money ticketPrice = new Money(new BigDecimal("50.00"), Currency.getInstance("USD"));
        Concert concert = new Concert(
                concertId, "Rock Night", LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(3), new Location("Arena", "123 Main St"),
                100, "The Band", "Rock", ticketPrice
        );

        Attendee attendee = new Attendee(UUID.randomUUID(), "Jane Doe", "jane@example.com", "555-1234");
        List<Registration> registrations = new ArrayList<>();

        try (var scope = new OperationsTaskScope<>("RegisterAttendee",
                new OperationContext.NonTransactional(),
                new TaskCompletionHandler<Registration>() {
                    @Override
                    public void onSuccess(Registration value) {
                        System.out.println("Registration successful: " + value);
                        registrations.add(value);
                    }
                    @Override
                    public void onFailure(Throwable throwable) {
                        System.err.println("Registration failed: " + throwable.getMessage());
                    }
                })) {
            scope.fork(() -> {
                var regOpt = RegistrationOperations.registerForEvent(concert, attendee, registrations);
                if (regOpt.isPresent()) {
                    return new OperationResult.Success<>(regOpt.get());
                } else {
                    return new OperationResult.Failure<>(new IllegalStateException("No capacity"));
                }
            });
            scope.join();
        }

        Money revenue = RegistrationOperations.calculateTotalRevenue(registrations);
        System.out.println("Total revenue: " + revenue.amount() + " " + revenue.currency());
        List<Event> events = List.of(concert);
        var upcoming = EventOperations.findEventsByTimeRange(
                events, LocalDateTime.now(), LocalDateTime.now().plusDays(1)
        );
        System.out.println("Upcoming events: " + upcoming);
    }
}
