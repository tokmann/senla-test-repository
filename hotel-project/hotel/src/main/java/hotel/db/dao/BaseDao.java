package hotel.db.dao;

import di.Inject;
import hotel.db.TransactionManager;
import hotel.exceptions.dao.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Базовый DAO-класс, предоставляющий общие методы для работы с базой данных.
 * Содержит вспомогательные методы для выполнения SQL-запросов и управления транзакциями.
 */
public abstract class BaseDao {

    @Inject
    protected TransactionManager transactionManager;

    /**
     * Выполняет SQL-запрос на вставку данных и возвращает сгенерированный идентификатор.
     * @param sql SQL-запрос для выполнения
     * @param params параметры для подстановки в запрос
     * @return сгенерированный идентификатор
     */
    protected long insertAndGetGeneratedId(String sql, Object... params) {
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Вставка не выполнена, нет затронутых строк");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new DaoException("Не удалось получить сгенерированный идентификатор");
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка выполнения операции вставки: " + sql, e);
        }
    }

    /**
     * Выполняет SQL-запрос на обновление или удаление данных.
     * @param sql SQL-запрос для выполнения
     * @param params параметры для подстановки в запрос
     */
    protected void executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Ошибка выполнения операции обновления: " + sql, e);
        }
    }

    /**
     * Выполняет SQL-запрос и возвращает один результат.
     * @param <T> тип возвращаемого объекта
     * @param sql SQL-запрос для выполнения
     * @param mapper функция преобразования ResultSet в объект
     * @param params параметры для подстановки в запрос
     * @return объект результата или null, если данных нет
     */
    protected <T> T findOne(String sql, RowMapper<T> mapper, Object... params) {
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка выполнения операции поиска одного результата: " + sql, e);
        }
    }

    /**
     * Выполняет SQL-запрос и возвращает список результатов.
     *
     * @param <T> тип объектов в списке
     * @param sql SQL-запрос для выполнения
     * @param mapper функция преобразования ResultSet в объект
     * @param params параметры для подстановки в запрос
     * @return список объектов результатов
     * @throws DaoException если произошла ошибка при выполнении запроса
     */
    protected <T> List<T> findMany(String sql, RowMapper<T> mapper, Object... params) {
        List<T> results = new ArrayList<>();
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка выполнения операции поиска множества результатов: " + sql, e);
        }
        return results;
    }

    /**
     * Выполняет SQL-запрос для подсчета количества записей.
     * @param sql SQL-запрос для выполнения
     * @param params параметры для подстановки в запрос
     * @return количество записей
     * @throws DaoException если произошла ошибка при выполнении запроса
     */
    protected int count(String sql, Object... params) {
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка выполнения операции подсчета: " + sql, e);
        }
    }

    /**
     * Устанавливает параметры в PreparedStatement для выполнения запроса.
     * @param ps PreparedStatement для установки параметров
     * @param params параметры для подстановки
     * @throws SQLException если произошла ошибка при установке параметров
     */
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    /**
     * Функциональный интерфейс для преобразования ResultSet в объект доменной модели.
     * @param <T> тип объекта для преобразования
     */
    @FunctionalInterface
    protected interface RowMapper<T> {
        /**
         * Преобразует строку ResultSet в объект доменной модели.
         * @param rs ResultSet с данными
         * @return объект модели
         * @throws SQLException если произошла ошибка при чтении данных
         */
        T map(ResultSet rs) throws SQLException;
    }
}
