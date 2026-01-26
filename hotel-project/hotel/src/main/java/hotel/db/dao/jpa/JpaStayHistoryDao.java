package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.TransactionManager;
import hotel.db.interfaces.StayHistoryRepository;
import hotel.exceptions.rooms.RoomException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.model.Room;
import hotel.model.StayHistory;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class JpaStayHistoryDao implements StayHistoryRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaStayHistoryDao.class);

    @Inject
    private EntityManagerContext entityManagerContext;

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }

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
}
