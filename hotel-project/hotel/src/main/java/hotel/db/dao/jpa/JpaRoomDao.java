package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.TransactionManager;
import hotel.db.interfaces.RoomRepository;
import hotel.exceptions.rooms.RoomException;
import hotel.model.Room;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Component
public class JpaRoomDao implements RoomRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaRoomDao.class);

    @Inject
    private EntityManagerContext entityManagerContext;

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }

    @Override
    public Room save(Room room) {
        try {
            EntityManager em = getEntityManager();
            if (room.getId() == 0) {
                em.persist(room);
            } else {
                room = em.merge(room);
            }
            return room;
        } catch (Exception e) {
            log.error("Ошибка при сохранении комнаты", e);
            throw new RoomException("Ошибка при сохранении комнаты", e);
        }
    }

    @Override
    public void delete(Room room) {
        try {
            EntityManager em = getEntityManager();
            Room managedRoom = em.find(Room.class, room.getId());
            if (managedRoom != null) {
                em.remove(managedRoom);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении комнаты", e);
            throw new RoomException("Ошибка при удалении комнаты", e);
        }
    }

    @Override
    public List<Room> findAll() {
        try {
            return getEntityManager().createQuery(
                    JpaQueryConstants.SELECT_ALL_ROOMS,
                    Room.class
            ).getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении списка комнат", e);
            throw new RoomException("Ошибка при получении списка комнат", e);
        }
    }

    @Override
    public Optional<Room> findById(long id) {
        try {
            Room room = getEntityManager().find(Room.class, id);
            return Optional.ofNullable(room);
        } catch (Exception e) {
            log.error("Ошибка при поиске комнаты по ID: {}", id, e);
            throw new RoomException("Ошибка при поиске комнаты по ID: " + id, e);
        }
    }

    @Override
    public Optional<Room> findByNumber(int number) {
        try {
            List<Room> rooms = getEntityManager().createQuery(
                            JpaQueryConstants.SELECT_ROOM_BY_NUMBER,
                            Room.class
                    ).setParameter(JpaQueryConstants.PARAM_NUMBER, number)
                    .getResultList();
            return rooms.isEmpty() ? Optional.empty() : Optional.of(rooms.get(0));
        } catch (Exception e) {
            log.error("Ошибка при поиске комнаты по номеру: {}", number, e);
            throw new RoomException("Ошибка при поиске комнаты по номеру: " + number, e);
        }
    }

    @Override
    public int countFree() {
        try {
            return Math.toIntExact(getEntityManager().createQuery(
                    JpaQueryConstants.COUNT_FREE_ROOMS,
                    Long.class
            ).getSingleResult());
        } catch (Exception e) {
            log.error("Ошибка при подсчете свободных комнат", e);
            throw new RoomException("Ошибка при подсчете свободных комнат", e);
        }
    }
}
