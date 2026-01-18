package hotel.db.dao.jdbc;

import di.Component;
import di.Inject;
import hotel.constants.SqlConstants;
import hotel.db.dao.AbstractDao;
import hotel.db.interfaces.GuestRepository;
import hotel.db.interfaces.GuestServiceRepository;
import hotel.db.interfaces.RoomRepository;
import hotel.model.Guest;
import hotel.model.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с гостями отеля.
 * Предоставляет методы для CRUD операций и поиска гостей.
 */
@Component
public class JdbcGuestDao extends AbstractDao<Guest> implements GuestRepository {

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private GuestServiceRepository guestServiceRepository;

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
    public void loadServicesForGuest(Guest guest) {
        if (guest.getId() != 0) {
            List<Service> services = guestServiceRepository.findServicesByGuestId(guest.getId());
            guest.setServices(services != null ? services : new ArrayList<>());
        }
    }

    /**
     * Преобразует ResultSet в объект Guest.
     * @param rs ResultSet с данными гостя
     * @return объект Guest
     */
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
