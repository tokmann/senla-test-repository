package hotel.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

/**
 * Контекст для управления экземпляром EntityManager в рамках потока.
 * Использует ThreadLocal для хранения и повторного использования EntityManager.
 */
@Component
public class EntityManagerContext {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }
}
