package com.example.core.common.utilities;

import java.util.function.Function;
import java.util.function.Supplier;

public sealed interface Result<T> {
    record Success<T>(T value) implements Result<T> { }
    record Failure<T>(String reason) implements Result<T> { }

    static <T> Result<T> success(T value) { return new Success<>(value); }
    static <T> Result<T> failure(String reason) { return new Failure<>(reason); }

    static <T> Result<T> of(Supplier<T> supplier) {
        try { return success(supplier.get()); }
        catch (Exception e) { return failure(e.getMessage()); }
    }

    default <U> Result<U> map(Function<T, U> mapper) {
        return switch (this) {
            case Success<T> s -> success(mapper.apply(s.value));
            case Failure<T> f -> failure(f.reason);
        };
    }

    default T orElseThrow() {
        return switch (this) {
            case Success<T> s -> s.value;
            case Failure<T> f -> throw new RuntimeException(f.reason);
        };
    }

    default T orElse(T defaultValue) {
        return switch (this) {
            case Success<T> s -> s.value;
            case Failure<T> f -> defaultValue;
        };
    }

    default boolean isSuccess() { return this instanceof Success; }
    default boolean isFailure() { return this instanceof Failure; }
}
