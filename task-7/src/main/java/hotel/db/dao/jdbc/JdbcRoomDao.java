package task_11.db.dao.jdbc;

import di.Component;
import task_11.constants.SqlConstants;
import task_11.db.dao.AbstractDao;
import task_11.db.interfaces.RoomRepository;
import task_11.model.Room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с комнатами отеля.
 * Предоставляет методы для CRUD операций и поиска комнат.
 */
@Component
public class JdbcRoomDao extends AbstractDao<Room> implements RoomRepository {

    @Override
    public Room save(Room room) {
        if (room.getId() == 0) {
            long id = insertAndGetGeneratedId(SqlConstants.INSERT_ROOM,
                    room.getNumber(),
                    room.getCapacity(),
                    room.getPrice(),
                    room.getStars(),
                    room.getHistorySize());
            room.setId(id);
        } else {
            executeUpdate(SqlConstants.UPDATE_ROOM,
                    room.getNumber(),
                    room.getCapacity(),
                    room.getPrice(),
                    room.getStars(),
                    room.isOccupied(),
                    room.isUnderMaintenance(),
                    room.isStatusChangeEnabled(),
                    room.getHistorySize(),
                    room.getCheckInDate(),
                    room.getCheckOutDate(),
                    room.getId());
        }
        return room;
    }

    @Override
    public void delete(Room room) {
        executeUpdate(SqlConstants.DELETE_ROOM, room.getId());
    }

    @Override
    public List<Room> findAll() {
        return findMany(SqlConstants.SELECT_ALL_ROOMS, this::mapRoom);
    }

    @Override
    public Optional<Room> findById(long id) {
        Room room = findOne(SqlConstants.SELECT_ROOM_BY_ID, this::mapRoom, id);
        return Optional.ofNullable(room);
    }

    @Override
    public Optional<Room> findByNumber(int number) {
        Room room = findOne(SqlConstants.SELECT_ROOM_BY_NUMBER, this::mapRoom, number);
        return Optional.ofNullable(room);
    }

    @Override
    public int countFree() {
        return count(SqlConstants.COUNT_FREE_ROOMS);
    }

    /**
     * Преобразует ResultSet в объект Room.
     * @param rs ResultSet с данными комнаты
     * @return объект Room
     */
    private Room mapRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getLong("id"));
        room.setNumber(rs.getInt("number"));
        room.setCapacity(rs.getInt("capacity"));
        room.setPrice(rs.getDouble("price"));
        room.setStars(rs.getInt("stars"));
        room.setOccupied(rs.getBoolean("is_occupied"));
        room.setUnderMaintenance(rs.getBoolean("under_maintenance"));
        room.setStatusChangeEnabled(rs.getBoolean("status_change_enabled"));
        room.setHistorySize(rs.getInt("history_size"));
        room.setCheckInDate(rs.getObject("check_in_date", LocalDate.class));
        room.setCheckOutDate(rs.getObject("check_out_date", LocalDate.class));
        return room;
    }
}
