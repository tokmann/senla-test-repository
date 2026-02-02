package hotel.db.dao.jpa;

import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.interfaces.StayHistoryRepository;
import hotel.exceptions.rooms.RoomException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.model.Room;
import hotel.model.StayHistory;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO-класс для работы с историей проживания через JPA.
 */
@Repository
public class JpaStayHistoryDao implements StayHistoryRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaStayHistoryDao.class);

    private final EntityManagerContext entityManagerContext;

    public JpaStayHistoryDao(EntityManagerContext entityManagerContext) {
        this.entityManagerContext = entityManagerContext;
    }

    /**
     * Добавляет запись в историю проживания для комнаты.
     * @param roomId идентификатор комнаты
     * @param entry текст записи
     */
    @Override
    public void addEntry(long roomId, String entry) {
        try {
            Room room = getEntityManager().find(Room.class, roomId);
            if (room == null) {
                throw new RoomNotFoundException(roomId);
            }

            StayHistory history = new StayHistory();
            history.setRoom(room);
            history.setEntry(entry);

            getEntityManager().persist(history);

            log.info("Добавлена запись в историю для комнаты ID {}", roomId);
        } catch (Exception e) {
            log.error("Ошибка при добавлении записи в историю для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при добавлении записи в историю для комнаты ID " + roomId, e);
        }
    }

    /**
     * Получает последние записи истории для комнаты.
     * @param roomId идентификатор комнаты
     * @param limit максимальное количество записей
     * @return список записей истории
     */
    @Override
    public List<String> findByRoomId(long roomId, int limit) {
        try {
            return getEntityManager().createQuery(
                            JpaQueryConstants.SELECT_HISTORY_ENTRIES_BY_ROOM_ID,
                            String.class
                    ).setParameter(JpaQueryConstants.PARAM_ROOM_ID, roomId)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении истории для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при получении истории для комнаты ID " + roomId, e);
        }
    }

    /**
     * Удаляет всю историю для указанной комнаты.
     * @param roomId идентификатор комнаты
     */
    @Override
    public void deleteByRoomId(long roomId) {
        try {
            getEntityManager().createQuery(
                            JpaQueryConstants.DELETE_HISTORY_BY_ROOM_ID
                    ).setParameter(JpaQueryConstants.PARAM_ROOM_ID, roomId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error("Ошибка при удалении истории для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при удалении истории для комнаты ID " + roomId, e);
        }
    }

    /**
     * Удаляет самую старую запись истории для комнаты.
     * @param roomId идентификатор комнаты
     */
    @Override
    public void deleteOldestEntryForRoom(long roomId) {
        try {
            getEntityManager().createQuery(
                            JpaQueryConstants.DELETE_OLDEST_HISTORY_ENTRY_BY_ROOM_ID
                    ).setParameter(JpaQueryConstants.PARAM_ROOM_ID, roomId)
                    .executeUpdate();
        } catch (Exception e) {
            log.error("Ошибка при удалении самой старой записи истории для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при удалении самой старой записи истории для комнаты ID " + roomId, e);
        }
    }

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }
}
