package com.example.core.operations;

import com.example.core.domain.Money;
import java.math.BigDecimal;
import java.util.List;

public final class MoneyOperations {
    private MoneyOperations() {}

    public static Money add(Money a, Money b) {
        if (!a.currency().equals(b.currency())) throw new IllegalArgumentException("Cannot add different currencies");
        return new Money(a.amount().add(b.amount()), a.currency());
    }

    public static Money subtract(Money a, Money b) {
        if (!a.currency().equals(b.currency())) throw new IllegalArgumentException("Cannot subtract different currencies");
        BigDecimal result = a.amount().subtract(b.amount());
        if (result.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Result would be negative");
        return new Money(result, a.currency());
    }

    public static Money sum(List<Money> amounts) {
        if (amounts.isEmpty()) return new Money(BigDecimal.ZERO, java.util.Currency.getInstance("USD"));
        java.util.Currency currency = amounts.getFirst().currency();
        if (!amounts.stream().allMatch(m -> m.currency().equals(currency))) {
            throw new IllegalArgumentException("All amounts must have the same currency");
        }
        BigDecimal total = amounts.stream().map(Money::amount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Money(total, currency);
    }
}
