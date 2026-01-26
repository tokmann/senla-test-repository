package hotel.db.dao.jpa;

import di.Component;
import di.Inject;
import hotel.db.EntityManagerContext;
import hotel.db.TransactionManager;
import hotel.db.interfaces.ServiceRepository;
import hotel.exceptions.services.ServiceException;
import hotel.model.Service;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Component
public class JpaServiceDao implements ServiceRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaServiceDao.class);

    @Inject
    private EntityManagerContext entityManagerContext;

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }

    @Override
    public Service save(Service service) {
        try {
            EntityManager em = getEntityManager();
            if (service.getId() == 0) {
                em.persist(service);
            } else {
                service = em.merge(service);
            }
            return service;
        } catch (Exception e) {
            log.error("Ошибка при сохранении услуги", e);
            throw new ServiceException("Ошибка при сохранении услуги", e);
        }
    }

    @Override
    public void delete(Service service) {
        try {
            EntityManager em = getEntityManager();
            Service managedService = em.find(Service.class, service.getId());
            if (managedService != null) {
                em.remove(managedService);
            }
        } catch (Exception e) {
            log.error("Ошибка при удалении услуги", e);
            throw new ServiceException("Ошибка при удалении услуги", e);
        }
    }

    @Override
    public List<Service> findAll() {
        try {
            String jpql = "SELECT s FROM Service s";
            return getEntityManager().createQuery(jpql, Service.class).getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении списка услуг", e);
            throw new ServiceException("Ошибка при получении списка услуг", e);
        }
    }

    @Override
    public Optional<Service> findById(long id) {
        try {
            Service service = getEntityManager().find(Service.class, id);
            return Optional.ofNullable(service);
        } catch (Exception e) {
            log.error("Ошибка при поиске услуги по ID: {}", id, e);
            throw new ServiceException("Ошибка при поиске услуги по ID: " + id, e);
        }
    }

    @Override
    public Optional<Service> findByName(String name) {
        try {
            String jpql = "SELECT s FROM Service s WHERE s.name = :name";
            List<Service> services = getEntityManager().createQuery(jpql, Service.class)
                    .setParameter("name", name)
                    .getResultList();
            return services.isEmpty() ? Optional.empty() : Optional.of(services.get(0));
        } catch (Exception e) {
            log.error("Ошибка при поиске услуги по названию: {}", name, e);
            throw new ServiceException("Ошибка при поиске услуги по названию: " + name, e);
        }
    }
}