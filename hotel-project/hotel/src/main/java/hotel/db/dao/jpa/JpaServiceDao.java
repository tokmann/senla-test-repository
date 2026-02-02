package hotel.db.dao.jpa;

import hotel.constants.JpaQueryConstants;
import hotel.db.EntityManagerContext;
import hotel.db.interfaces.ServiceRepository;
import hotel.exceptions.services.ServiceException;
import hotel.model.Service;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaServiceDao implements ServiceRepository {

    private static final Logger log = LoggerFactory.getLogger(JpaServiceDao.class);

    private final EntityManagerContext entityManagerContext;

    public JpaServiceDao(EntityManagerContext entityManagerContext) {
        this.entityManagerContext = entityManagerContext;
    }

    /**
     * Сохраняет или обновляет услугу в базе данных.
     * @param service услуга для сохранения
     * @return сохраненная услуга
     */
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

    /**
     * Удаляет услугу из базы данных.
     * @param service услуга для удаления
     */
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

    /**
     * Возвращает список всех услуг.
     * @return список всех услуг
     */
    @Override
    public List<Service> findAll() {
        try {
            return getEntityManager().createQuery(
                    JpaQueryConstants.SELECT_ALL_SERVICES,
                    Service.class
            ).getResultList();
        } catch (Exception e) {
            log.error("Ошибка при получении списка услуг", e);
            throw new ServiceException("Ошибка при получении списка услуг", e);
        }
    }

    /**
     * Находит услугу по идентификатору.
     * @param id идентификатор услуги
     * @return объект услуги или пустой Optional, если не найдена
     */
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

    /**
     * Находит услугу по названию.
     * @param name название услуги
     * @return объект услуги или пустой Optional, если не найдена
     */
    @Override
    public Optional<Service> findByName(String name) {
        try {
            List<Service> services = getEntityManager().createQuery(
                            JpaQueryConstants.SELECT_SERVICE_BY_NAME,
                            Service.class
                    ).setParameter(JpaQueryConstants.PARAM_NAME, name)
                    .getResultList();
            return services.isEmpty() ? Optional.empty() : Optional.of(services.get(0));
        } catch (Exception e) {
            log.error("Ошибка при поиске услуги по названию: {}", name, e);
            throw new ServiceException("Ошибка при поиске услуги по названию: " + name, e);
        }
    }

    private EntityManager getEntityManager() {
        return entityManagerContext.getEntityManager();
    }
}