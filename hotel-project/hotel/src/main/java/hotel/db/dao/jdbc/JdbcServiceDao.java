package hotel.db.dao.jdbc;

import di.Component;
import hotel.constants.SqlConstants;
import hotel.db.dao.AbstractDao;
import hotel.model.Service;
import hotel.db.interfaces.ServiceRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * DAO для работы с услугами отеля.
 * Предоставляет методы для CRUD операций с услугами.
 */
@Component
public class JdbcServiceDao extends AbstractDao<Service> implements ServiceRepository {

    @Override
    public Service save(Service service) {
        if (service.getId() == 0) {
            long id = insertAndGetGeneratedId(SqlConstants.INSERT_SERVICE,
                    service.getName(),
                    service.getDescription(),
                    service.getPrice(),
                    service.getDate());
            service.setId(id);
        } else {
            executeUpdate(SqlConstants.UPDATE_SERVICE,
                    service.getName(),
                    service.getDescription(),
                    service.getPrice(),
                    service.getDate(),
                    service.getId());
        }
        return service;
    }

    @Override
    public void delete(Service service) {
        executeUpdate(SqlConstants.DELETE_SERVICE, service.getId());
    }

    @Override
    public List<Service> findAll() {
        return findMany(SqlConstants.SELECT_ALL_SERVICES, this::mapService);
    }

    @Override
    public Optional<Service> findById(long id) {
        Service service = findOne(SqlConstants.SELECT_SERVICE_BY_ID, this::mapService, id);
        return Optional.ofNullable(service);
    }

    @Override
    public Optional<Service> findByName(String name) {
        Service service = findOne(SqlConstants.SELECT_SERVICE_BY_NAME, this::mapService, name);
        return Optional.ofNullable(service);
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
}
