package com.example.core.common.utilities.operations;

import com.example.core.common.utilities.persistence.TransactionManager;

public sealed interface OperationContext permits
        OperationContext.Transactional,
        OperationContext.NonTransactional {
    record Transactional(TransactionManager transactionManager) implements OperationContext {}
    record NonTransactional() implements OperationContext {}
}