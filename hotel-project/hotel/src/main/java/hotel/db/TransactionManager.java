package hotel.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Менеджер транзакций для управления транзакциями JPA.
 * Предоставляет методы для начала, коммита и отката транзакций.
 */
@Component
public class TransactionManager {

    private static final Logger log = LoggerFactory.getLogger(TransactionManager.class);

    private final EntityManagerContext entityManagerContext;

    public TransactionManager(EntityManagerContext entityManagerContext) {
        this.entityManagerContext = entityManagerContext;
    }

    /**
     * Начинает новую транзакцию, если она ещё не активна.
     */
    public void beginTransaction() {
        EntityManager em = entityManagerContext.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        if (!tx.isActive()) {
            tx.begin();
            log.debug("Транзакция начата");
        }
    }

    /**
     * Фиксирует текущую транзакцию.
     * В случае ошибки выполняет откат и очищает контекст.
     */
    public void commitTransaction() {
        EntityManager em = entityManagerContext.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        if (tx.isActive()) {
            try {
                tx.commit();
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

    /**
     * Откатывает текущую транзакцию и очищает контекст.
     */
    public void rollbackTransaction() {
        EntityManager em = entityManagerContext.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        if (tx.isActive()) {
            try {
                tx.rollback();
                log.warn("Транзакция откатилась");
            } catch (Exception e) {
                log.error("Ошибка при откате транзакции", e);
            } finally {
                entityManagerContext.clear();
            }
        }
    }
}
