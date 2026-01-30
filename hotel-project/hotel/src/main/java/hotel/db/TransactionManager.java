package hotel.db;

import di.Component;
import di.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component
public class TransactionManager {

    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);

    @Inject
    private EntityManagerContext entityManagerContext;

    public void beginTransaction() {
        EntityManager entityManager = entityManagerContext.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        if (!transaction.isActive()) {
            transaction.begin();
            log.debug("Транзакция начата");
        } else {
            log.debug("Используется существующая транзакция");
        }
    }

    public void commitTransaction() {
        EntityManager entityManager = entityManagerContext.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        if (transaction.isActive()) {
            try {
                transaction.commit();
                log.debug("Транзакция успешно завершена");
            } catch (Exception e) {
                log.error("Ошибка при коммите транзакции", e);
                rollbackTransaction();
                throw new RuntimeException("Ошибка при коммите транзакции", e);
            } finally {
                entityManagerContext.clear();
            }
        }
    }

    public void rollbackTransaction() {
        EntityManager entityManager = entityManagerContext.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        if (transaction.isActive()) {
            try {
                transaction.rollback();
                log.warn("Транзакция откатилась");
            } catch (Exception e) {
                log.error("Ошибка при откате транзакции", e);
            } finally {
                entityManagerContext.clear();
            }
        }
    }
}
