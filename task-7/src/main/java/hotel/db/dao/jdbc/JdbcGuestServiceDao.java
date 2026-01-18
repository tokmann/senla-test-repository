package task_11.db.dao.jdbc;

import di.Component;
import task_11.constants.SqlConstants;
import task_11.db.dao.BaseDao;
import task_11.db.interfaces.GuestServiceRepository;
import task_11.model.Guest;
import task_11.model.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * DAO для работы со связями между гостями и услугами.
 * Предоставляет методы для управления услугами, назначенными гостям.
 */
@Component
public class JdbcGuestServiceDao extends BaseDao implements GuestServiceRepository {

    @Override
    public void addServiceToGuest(long guestId, long serviceId) {
        executeUpdate(SqlConstants.INSERT_GUEST_SERVICE, guestId, serviceId);
    }

    @Override
    public void removeServiceFromGuest(long guestId, long serviceId) {
        executeUpdate(SqlConstants.DELETE_GUEST_SERVICE, guestId, serviceId);
    }

    @Override
    public List<Service> findServicesByGuestId(long guestId) {
        return findMany(SqlConstants.SELECT_SERVICES_BY_GUEST_ID, this::mapService, guestId);
    }

    @Override
    public List<Guest> findGuestsByServiceId(long serviceId) {
        return findMany(SqlConstants.SELECT_GUESTS_BY_SERVICE_ID, this::mapGuest, serviceId);
    }

    /**
     * Преобразует ResultSet в объект Service.
     * @param rs ResultSet с данными услуги
     * @return объект Service
     */
    private Service mapService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getLong("id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        service.setPrice(rs.getDouble("price"));
        service.setDate(rs.getObject("date", LocalDate.class));
        return service;
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
