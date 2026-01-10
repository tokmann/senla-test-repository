package task_11.db.dao.jdbc;

import di.Component;
import di.Inject;
import task_11.constants.SqlConstants;
import task_11.db.dao.AbstractDao;
import task_11.db.interfaces.GuestRepository;
import task_11.db.interfaces.RoomRepository;
import task_11.model.Guest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class JdbcGuestDao extends AbstractDao<Guest> implements GuestRepository {

    @Inject
    private RoomRepository roomRepository;

    @Override
    public Guest save(Guest guest) {
        if (guest.getId() == 0) {
            long id = insertAndGetGeneratedId(SqlConstants.INSERT_GUEST,
                    guest.getAge(),
                    guest.getFirstName(),
                    guest.getSecondName(),
                    guest.getRoomId());
            guest.setId(id);
        } else {
            executeUpdate(SqlConstants.UPDATE_GUEST,
                    guest.getAge(),
                    guest.getFirstName(),
                    guest.getSecondName(),
                    guest.getRoomId(),
                    guest.getId());
        }
        return guest;
    }

    @Override
    public void delete(Guest guest) {
        executeUpdate(SqlConstants.DELETE_GUEST, guest.getId());
    }

    @Override
    public Optional<Guest> findById(long id) {
        Guest guest = findOne(SqlConstants.SELECT_GUEST_BY_ID, this::mapGuest, id);
        return Optional.ofNullable(guest);
    }

    @Override
    public List<Guest> findAll() {
        return findMany(SqlConstants.SELECT_ALL_GUESTS, this::mapGuest);
    }

    @Override
    public List<Guest> findByRoomId(long roomId) {
        return findMany(SqlConstants.SELECT_GUESTS_BY_ROOM_ID, this::mapGuest, roomId);
    }

    @Override
    public int count() {
        return count(SqlConstants.COUNT_GUESTS);
    }

    @Override
    public void loadRoomForGuest(Guest guest) {
        if (guest.getRoomId() != null) {
            roomRepository.findById(guest.getRoomId()).ifPresent(guest::setRoom);
        }
    }

    @Override
    public void loadServicesForGuest(Guest guest) {}

    private Guest mapGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setId(rs.getLong("id"));
        guest.setAge(rs.getInt("age"));
        guest.setFirstName(rs.getString("first_name"));
        guest.setSecondName(rs.getString("second_name"));
        guest.setRoomId(rs.getLong("room_id"));
        return guest;
    }
}
