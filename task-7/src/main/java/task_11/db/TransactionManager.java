package task_11.db;

import di.Component;
import di.Inject;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class TransactionManager {

    private static final ThreadLocal<Boolean> inTransaction = new ThreadLocal<>();
    private final Connection connection;

    public TransactionManager() {
        this.connection = ConnectionProvider.getInstance().getConnection();
    }

    public void beginTransaction() {
        if (Boolean.TRUE.equals(inTransaction.get())) {
            throw new IllegalStateException("Transaction already active");
        }
        try {
            inTransaction.set(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to begin transaction", e);
        }
    }

    public void commitTransaction() {
        if (!Boolean.TRUE.equals(inTransaction.get())) {
            throw new IllegalStateException("No active transaction to commit");
        }
        try {
            connection.commit();
            inTransaction.remove();
        } catch (SQLException e) {
            rollbackTransaction();
            throw new RuntimeException("Failed to commit transaction", e);
        }
    }

    public void rollbackTransaction() {
        if (!Boolean.TRUE.equals(inTransaction.get())) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rollback transaction", e);
        } finally {
            inTransaction.remove();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
