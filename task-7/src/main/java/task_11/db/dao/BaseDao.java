package task_11.db.dao;

import di.Inject;
import task_11.db.TransactionManager;
import task_11.exceptions.dao.DaoException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao {

    @Inject
    protected TransactionManager transactionManager;

    protected long insertAndGetGeneratedId(String sql, Object... params) {
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameters(ps, params);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Insert failed, no rows affected");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new DaoException("Failed to retrieve generated ID");
            }
        } catch (SQLException e) {
            throw new DaoException("Insert operation failed: " + sql, e);
        }
    }

    protected void executeUpdate(String sql, Object... params) {
        try (PreparedStatement ps = transactionManager.getConnection().prepareStatement(sql)) {
            setParameters(ps, params);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Update operation failed: " + sql, e);
        }
    }

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
            throw new DaoException("Find one operation failed: " + sql, e);
        }
    }

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
            throw new DaoException("Find many operation failed: " + sql, e);
        }
        return results;
    }

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
            throw new DaoException("Count operation failed: " + sql, e);
        }
    }

    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    @FunctionalInterface
    protected interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
