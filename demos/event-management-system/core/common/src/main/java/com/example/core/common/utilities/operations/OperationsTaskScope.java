package com.example.core.common.utilities.operations;

import com.example.core.common.utilities.completion.OperationResult;
import com.example.core.common.utilities.completion.TaskCompletionHandler;
import java.sql.SQLException;
import java.util.concurrent.StructuredTaskScope;

public class OperationsTaskScope<T> extends StructuredTaskScope<OperationResult<T>> {
    private final String operationName;
    private final OperationContext context;
    private final TaskCompletionHandler<T> completionHandler;
    private volatile boolean shouldShutdown = false;

    public OperationsTaskScope(String operationName, OperationContext context, TaskCompletionHandler<T> handler) {
        super(operationName + "TaskScope", Thread.ofVirtual().factory());
        this.operationName = operationName;
        this.context = context;
        this.completionHandler = handler;
    }

    @Override
    protected void handleComplete(Subtask<? extends OperationResult<T>> subtask) {
        if (subtask.state() == Subtask.State.FAILED) {
            handleFailure(subtask.exception());
        } else if (subtask.state() == Subtask.State.SUCCESS) {
            handleSuccess(subtask.get());
        }
    }

    private void handleFailure(Throwable error) {
        if (error instanceof SQLException && context instanceof OperationContext.Transactional tx) {
            tx.transactionManager().rollbackIfActive();
        }
        completionHandler.onFailure(error);
        shouldShutdown = true;
        shutdown();
    }

    private void handleSuccess(OperationResult<T> result) {
        switch (result) {
            case OperationResult.Success<T> success -> completionHandler.onSuccess(success.value());
            case OperationResult.Failure<T> failure -> {
                completionHandler.onFailure(failure.error());
                shouldShutdown = true;
                shutdown();
            }
        }
    }
}