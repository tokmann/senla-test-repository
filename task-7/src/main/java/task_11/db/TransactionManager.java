package task_11.db;

import di.Component;
import task_11.exceptions.db.TransactionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Менеджер транзакций для управления транзакциями базы данных.
 * Обеспечивает начало, подтверждение и откат транзакций.
 * Использует ThreadLocal для хранения состояния транзакции в рамках текущего потока.
 */
@Component
public class TransactionManager {

    private static final ThreadLocal<Boolean> inTransaction = new ThreadLocal<>();
    private final Connection connection;

    /**
     * Конструктор менеджера транзакций.
     * Инициализирует соединение с базой данных через ConnectionProvider.
     */
    public TransactionManager() {
        this.connection = ConnectionProvider.getInstance().getConnection();
    }

    /**
     * Начинает новую транзакцию.
     */
    public void beginTransaction() {
        if (Boolean.TRUE.equals(inTransaction.get())) {
            throw new TransactionException("Транзакция уже активна");
        }
        try {
            inTransaction.set(true);
        } catch (Exception e) {
            throw new TransactionException("Не удалось начать транзакцию", e);
        }
    }

    /**
     * Подтверждает текущую транзакцию.
     */
    public void commitTransaction() {
        if (!Boolean.TRUE.equals(inTransaction.get())) {
            throw new TransactionException("Нет активной транзакции для подтверждения");
        }
        try {
            connection.commit();
            inTransaction.remove();
        } catch (SQLException e) {
            rollbackTransaction();
            throw new TransactionException("Не удалось подтвердить транзакцию", e);
        }
    }

    /**
     * Откатывает текущую транзакцию.
     */
    public void rollbackTransaction() {
        if (!Boolean.TRUE.equals(inTransaction.get())) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new TransactionException("Не удалось откатить транзакцию", e);
        } finally {
            inTransaction.remove();
        }
    }

    /**
     * Возвращает соединение с базой данных для выполнения SQL-запросов.
     * @return соединение с базой данных
     */
    public Connection getConnection() {
        return connection;
    }
}
