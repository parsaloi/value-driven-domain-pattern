package com.example.core.common.utilities.completion;

public interface TaskCompletionHandler<T> {
    void onSuccess(T value);
    void onFailure(Throwable throwable);
    default void onShutdown() {}
    default <U extends T> TaskCompletionHandler<U> andThen(TaskCompletionHandler<U> nextHandler) {
        return new TaskCompletionHandler<U>() {
            @Override
            public void onSuccess(U value) {
                TaskCompletionHandler.this.onSuccess(value);
                nextHandler.onSuccess(value);
            }
            @Override
            public void onFailure(Throwable throwable) {
                TaskCompletionHandler.this.onFailure(throwable);
                nextHandler.onFailure(throwable);
            }
            @Override
            public void onShutdown() {
                TaskCompletionHandler.this.onShutdown();
                nextHandler.onShutdown();
            }
        };
    }
}