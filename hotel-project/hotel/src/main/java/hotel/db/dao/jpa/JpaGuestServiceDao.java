package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
import hotel.db.EntityManagerContext;
import hotel.db.TransactionManager;
import hotel.db.interfaces.GuestServiceRepository;
import hotel.exceptions.guests.GuestNotFoundException;
import hotel.exceptions.services.ServiceException;
import hotel.exceptions.services.ServiceNotFoundException;
import hotel.model.Guest;
import hotel.model.Service;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component
public class JpaGuestServiceDao implements GuestServiceRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaGuestServiceDao.class);

    @Inject
    private TransactionManager transactionManager;

    @Inject
    private EntityManagerContext entityManagerContext;

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }

    @Override
    public void addServiceToGuest(long guestId, long serviceId) {
        transactionManager.beginTransaction();
        try {
            Guest guest = getEntityManager().find(Guest.class, guestId);
            Service service = getEntityManager().find(Service.class, serviceId);

            if (guest == null) {
                throw new GuestNotFoundException(guestId);
            }
            if (service == null) {
                throw new ServiceNotFoundException(String.valueOf(serviceId));
            }

            if (!guest.getServices().contains(service)) {
                guest.getServices().add(service);
            }

            if (!service.getGuests().contains(guest)) {
                service.getGuests().add(guest);
            }

            getEntityManager().merge(guest);
            getEntityManager().merge(service);

            transactionManager.commitTransaction();
            log.info("Услуга ID {} успешно добавлена гостю ID {}", serviceId, guestId);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка при добавлении услуги ID {} гостю ID {}", serviceId, guestId, e);
            throw new ServiceException("Ошибка при добавлении услуги гостю", e);
        }
    }

    @Override
    public void removeServiceFromGuest(long guestId, long serviceId) {
        transactionManager.beginTransaction();
        try {
            Guest guest = getEntityManager().find(Guest.class, guestId);
            Service service = getEntityManager().find(Service.class, serviceId);

            if (guest == null) {
                throw new GuestNotFoundException(guestId);
            }
            if (service == null) {
                throw new ServiceNotFoundException(String.valueOf(serviceId));
            }

            guest.getServices().remove(service);

            service.getGuests().remove(guest);

            getEntityManager().merge(guest);
            getEntityManager().merge(service);

            transactionManager.commitTransaction();
            log.info("Услуга ID {} успешно удалена у гостя ID {}", serviceId, guestId);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка при удалении услуги ID {} у гостя ID {}", serviceId, guestId, e);
            throw new ServiceException("Ошибка при удалении услуги у гостя", e);
        }
    }

    @Override
    public List<Service> findServicesByGuestId(long guestId) {
        try {
            Guest guest = getEntityManager().find(Guest.class, guestId);
            if (guest == null) {
                throw new GuestNotFoundException(guestId);
            }

            Hibernate.initialize(guest.getServices());
            return new ArrayList<>(guest.getServices());
        } catch (Exception e) {
            log.error("Ошибка при поиске услуг для гостя ID {}", guestId, e);
            throw new ServiceException("Ошибка при поиске услуг для гостя ID " + guestId, e);
        }
    }

    @Override
    public List<Guest> findGuestsByServiceId(long serviceId) {
        try {
            Service service = getEntityManager().find(Service.class, serviceId);
            if (service == null) {
                throw new ServiceNotFoundException(String.valueOf(serviceId));
            }

            Hibernate.initialize(service.getGuests());
            return new ArrayList<>(service.getGuests());
        } catch (Exception e) {
            log.error("Ошибка при поиске гостей для услуги ID {}", serviceId, e);
            throw new ServiceException("Ошибка при поиске гостей для услуги ID " + serviceId, e);
        }
    }
}
