package hotel.db.dao.jpa;

import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.interfaces.RoomRepository;
import hotel.exceptions.rooms.RoomException;
import hotel.model.Room;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * DAO-класс для работы с сущностью комнаты через JPA.
 */
@Repository
public class JpaRoomDao implements RoomRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaRoomDao.class);

    private final EntityManagerContext entityManagerContext;

    public JpaRoomDao(EntityManagerContext entityManagerContext) {
        this.entityManagerContext = entityManagerContext;
    }

    /**
     * Сохраняет или обновляет комнату в базе данных.
     * @param room комната для сохранения
     * @return сохраненная комната
     */
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

    /**
     * Удаляет комнату из базы данных.
     * @param room комната для удаления
     */
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

    /**
     * Возвращает список всех комнат.
     * @return список всех комнат
     */
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

    /**
     * Находит комнату по идентификатору.
     * @param id идентификатор комнаты
     * @return объект комнаты или пустой Optional, если не найдена
     */
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

    /**
     * Находит комнату по номеру.
     * @param number номер комнаты
     * @return объект комнаты или пустой Optional, если не найдена
     */
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

    /**
     * Подсчитывает количество свободных комнат.
     * @return количество свободных комнат
     */
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

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }
}
