package hotel.db.dao.jpa;

import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.interfaces.GuestRepository;
import hotel.exceptions.guests.GuestException;
import hotel.model.Guest;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA-реализация репозитория для работы с гостями.
 * Отвечает за сохранение, удаление и поиск гостей в базе данных.
 */
@Repository
public class JpaGuestDao implements GuestRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaGuestDao.class);

    private final EntityManagerContext entityManagerContext;

    public JpaGuestDao(EntityManagerContext entityManagerContext) {
        this.entityManagerContext = entityManagerContext;
    }

    /**
     * Сохраняет или обновляет гостя в базе данных.
     * @param guest гость для сохранения
     * @return сохраненный гость
     */
    @Override
    public Guest save(Guest guest) {
        try {
            EntityManager em = getEntityManager();
            if (guest.getId() == 0) {
                em.persist(guest);
            } else {
                guest = em.merge(guest);
            }
            return guest;
        } catch (Exception e) {
            log.error("Ошибка при сохранении гостя", e);
            throw new GuestException("Ошибка при сохранении гостя", e);
        }
    }

    /**
     * Удаляет гостя из базы данных.
     * @param guest гость для удаления
     */
    @Override
    public void delete(Guest guest) {
        try {
            EntityManager em = getEntityManager();
            Guest managedGuest = em.find(Guest.class, guest.getId());
            if (managedGuest != null) {
                em.remove(managedGuest);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении гостя", e);
            throw new GuestException("Ошибка при удалении гостя", e);
        }
    }

    /**
     * Находит гостя по идентификатору.
     * @param id идентификатор гостя
     * @return объект гостя или пустой Optional, если не найден
     */
    @Override
    public Optional<Guest> findById(long id) {
        try {
            Guest guest = getEntityManager().find(Guest.class, id);
            if (guest != null) {
                guest.getServices().size();
                if (guest.getRoom() != null) {
                    guest.getRoom().getId();
                }
            }
            return Optional.ofNullable(guest);
        } catch (Exception e) {
            log.error("Ошибка при поиске гостя по ID: {}", id, e);
            throw new GuestException("Ошибка при поиске гостя по ID: " + id, e);
        }
    }

    /**
     * Возвращает список всех гостей.
     * @return список всех гостей
     */
    @Override
    public List<Guest> findAll() {
        try {
            return getEntityManager().createQuery(
                    JpaQueryConstants.SELECT_ALL_GUESTS_WITH_SERVICES_AND_ROOM,
                    Guest.class
            ).getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении списка гостей", e);
            throw new GuestException("Ошибка при получении списка гостей", e);
        }
    }

    /**
     * Находит гостей по идентификатору комнаты.
     * @param roomId идентификатор комнаты
     * @return список гостей, проживающих в указанной комнате
     */
    @Override
    public List<Guest> findByRoomId(long roomId) {
        try {
            return getEntityManager().createQuery(
                            JpaQueryConstants.SELECT_GUESTS_BY_ROOM_ID,
                            Guest.class
                    ).setParameter(JpaQueryConstants.PARAM_ROOM_ID, roomId)
                    .getResultList();
        } catch (Exception e) {
            log.error("Ошибка при поиске гостей по ID комнаты: {}", roomId, e);
            throw new GuestException("Ошибка при поиске гостей по ID комнаты: " + roomId, e);
        }
    }

    /**
     * Подсчитывает общее количество гостей.
     * @return количество гостей
     * @throws GuestException при ошибке подсчета
     */
    @Override
    public int count() {
        try {
            return Math.toIntExact(getEntityManager().createQuery(
                    JpaQueryConstants.COUNT_ALL_GUESTS,
                    Long.class
            ).getSingleResult());
        } catch (Exception e) {
            log.error("Ошибка при подсчете количества гостей", e);
            throw new GuestException("Ошибка при подсчете количества гостей", e);
        }
    }

    /**
     * Загружает информацию о комнате для гостя.
     * @param guest гость, для которого нужно загрузить комнату
     */
    @Override
    public void loadRoomForGuest(Guest guest) {
        if (guest.getRoom() != null) {
            guest.getRoom().getNumber();
        }
    }

    /**
     * Загружает список услуг для гостя.
     * @param guest гость, для которого нужно загрузить услуги
     */
    @Override
    public void loadServicesForGuest(Guest guest) {
        guest.getServices().size();
    }

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }
}
