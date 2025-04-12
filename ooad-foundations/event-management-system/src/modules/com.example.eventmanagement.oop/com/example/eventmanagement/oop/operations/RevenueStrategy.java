package com.example.eventmanagement.oop.operations;

import com.example.eventmanagement.oop.domain.*;

import java.util.List;
import java.math.BigDecimal;
import java.util.Currency;

public interface RevenueStrategy {
    Money calculateRevenue(List<Registration> registrations);

    class DefaultRevenueStrategy implements RevenueStrategy {
        @Override
        public Money calculateRevenue(List<Registration> registrations) {
            Money total = new Money(BigDecimal.ZERO, Currency.getInstance("USD"));
            for (Registration reg : registrations) {
                if (reg.getStatus() == RegistrationStatus.CONFIRMED || reg.getStatus() == RegistrationStatus.ATTENDED) {
                    total = total.add(reg.getEvent().getFee());
                }
            }
            return total;
        }
    }
}
