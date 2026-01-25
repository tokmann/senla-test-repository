package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
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
    private TransactionManager transactionManager;

    @Inject
    private EntityManagerContext entityManagerContext;

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }

    @Override
    public void addEntry(long roomId, String entry) {
        transactionManager.beginTransaction();
        try {
            Room room = getEntityManager().find(Room.class, roomId);
            if (room == null) {
                throw new RoomNotFoundException(roomId);
            }

            StayHistory history = new StayHistory();
            history.setRoom(room);
            history.setEntry(entry);

            getEntityManager().persist(history);

            transactionManager.commitTransaction();
            log.info("Добавлена запись в историю для комнаты ID {}", roomId);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка при добавлении записи в историю для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при добавлении записи в историю для комнаты ID " + roomId, e);
        }
    }

    @Override
    public List<String> findByRoomId(long roomId, int limit) {
        try {
            String jpql = "SELECT h.entry FROM StayHistory h WHERE h.room.id = :roomId ORDER BY h.entryDate DESC";
            return getEntityManager().createQuery(jpql, String.class)
                    .setParameter("roomId", roomId)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении истории для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при получении истории для комнаты ID " + roomId, e);
        }
    }

    @Override
    public void deleteByRoomId(long roomId) {
        transactionManager.beginTransaction();
        try {
            String jpql = "DELETE FROM StayHistory h WHERE h.room.id = :roomId";
            getEntityManager().createQuery(jpql)
                    .setParameter("roomId", roomId)
                    .executeUpdate();

            transactionManager.commitTransaction();
            log.info("История для комнаты ID {} успешно удалена", roomId);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка при удалении истории для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при удалении истории для комнаты ID " + roomId, e);
        }
    }

    @Override
    public void deleteOldestEntryForRoom(long roomId) {
        transactionManager.beginTransaction();
        try {
            String jpql = "DELETE FROM StayHistory h WHERE h.id = (SELECT MIN(h2.id) FROM StayHistory h2 WHERE h2.room.id = :roomId)";
            getEntityManager().createQuery(jpql)
                    .setParameter("roomId", roomId)
                    .executeUpdate();

            transactionManager.commitTransaction();
            log.info("Самая старая запись истории для комнаты ID {} успешно удалена", roomId);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка при удалении самой старой записи истории для комнаты ID {}", roomId, e);
            throw new RoomException("Ошибка при удалении самой старой записи истории для комнаты ID " + roomId, e);
        }
    }
}
