package com.example.core.common.utilities.completion;

public sealed interface OperationResult<T> {
    record Success<T>(T value) implements OperationResult<T> {}
    record Failure<T>(Throwable error) implements OperationResult<T> {}
}