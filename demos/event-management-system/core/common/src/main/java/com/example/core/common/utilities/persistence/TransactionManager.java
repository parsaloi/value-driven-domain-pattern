package com.example.core.common.utilities.persistence;

import java.sql.Connection;
import java.sql.SQLException;

public final class TransactionManager implements AutoCloseable {
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private Connection connection;

    public void begin() throws SQLException {
        Connection conn = null; // In-memory demo, no actual connection
        connectionHolder.set(conn);
    }

    public void commit() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            // No-op for demo
        }
    }

    public void rollbackIfActive() {
        try {
            Connection conn = connectionHolder.get();
            if (conn != null) {
                // No-op for demo
            }
        } catch (Exception e) {
            // Log rollback failure (not implemented for demo)
        }
    }

    @Override
    public void close() {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            try {
                // No-op for demo
            } catch (Exception e) {
                // Log close failure (not implemented)
            } finally {
                connectionHolder.remove();
            }
        }
    }

    public Connection getConnection() {
        return connectionHolder.get();
    }
}