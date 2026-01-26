package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
import hotel.db.EntityManagerContext;
import hotel.db.TransactionManager;
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
            String jpql = "SELECT g FROM Guest g LEFT JOIN FETCH g.services LEFT JOIN FETCH g.room";
            return getEntityManager().createQuery(jpql, Guest.class).getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении списка гостей", e);
            throw new GuestException("Ошибка при получении списка гостей", e);
        }
    }

    @Override
    public List<Guest> findByRoomId(long roomId) {
        try {
            String jpql = "SELECT g FROM Guest g WHERE g.room.id = :roomId";
            return getEntityManager().createQuery(jpql, Guest.class)
                    .setParameter("roomId", roomId)
                    .getResultList();
        } catch (Exception e) {
            log.error("Ошибка при поиске гостей по ID комнаты: {}", roomId, e);
            throw new GuestException("Ошибка при поиске гостей по ID комнаты: " + roomId, e);
        }
    }

    @Override
    public int count() {
        try {
            String jpql = "SELECT COUNT(g) FROM Guest g";
            return Math.toIntExact(getEntityManager().createQuery(jpql, Long.class).getSingleResult());
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
