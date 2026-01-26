package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.interfaces.GuestRepository;
import hotel.exceptions.guests.GuestException;
import hotel.model.Guest;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Component
public class JpaGuestDao implements GuestRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaGuestDao.class);

    @Inject
    private EntityManagerContext entityManagerContext;

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }

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

    @Override
    public void loadRoomForGuest(Guest guest) {
        if (guest.getRoom() != null) {
            guest.getRoom().getNumber();
        }
    }

    @Override
    public void loadServicesForGuest(Guest guest) {
        guest.getServices().size();
    }
}
