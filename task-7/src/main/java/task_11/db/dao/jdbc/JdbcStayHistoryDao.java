package task_11.db.dao.jdbc;

import di.Component;
import task_11.constants.SqlConstants;
import task_11.db.dao.BaseDao;
import task_11.db.interfaces.StayHistoryRepository;

import java.util.List;

@Component
public class JdbcStayHistoryDao extends BaseDao implements StayHistoryRepository {

    @Override
    public void addEntry(long roomId, String entry) {
        executeUpdate(SqlConstants.INSERT_STAY_HISTORY, roomId, entry);
    }

    @Override
    public List<String> findByRoomId(long roomId, int limit) {
        return findMany(SqlConstants.SELECT_HISTORY_BY_ROOM_ID,
                rs -> rs.getString("history_entry"),
                roomId,
                limit);
    }

    @Override
    public void deleteByRoomId(long roomId) {
        executeUpdate(SqlConstants.DELETE_HISTORY_BY_ROOM_ID, roomId);
    }
}
