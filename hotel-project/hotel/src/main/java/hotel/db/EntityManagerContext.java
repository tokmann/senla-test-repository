package hotel.db;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

/**
 * Контекст для управления экземпляром EntityManager в рамках потока.
 * Использует ThreadLocal для хранения и повторного использования EntityManager.
 */
@Component
public class EntityManagerContext {

    private final ThreadLocal<EntityManager> entityManagerHolder =
            new ThreadLocal<>();

    private final EntityManagerFactoryProvider emfProvider;

    public EntityManagerContext(EntityManagerFactoryProvider emfProvider) {
        this.emfProvider = emfProvider;
    }

    /**
     * Возвращает текущий EntityManager для потока.
     * Если менеджер не существует или закрыт, создаёт новый экземпляр.
     * @return экземпляр EntityManager
     */
    public EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerHolder.get();

        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = emfProvider.createEntityManager();
            entityManagerHolder.set(entityManager);
        }

        return entityManager;
    }

    /**
     * Закрывает текущий EntityManager и удаляет его из контекста потока.
     */
    public void clear() {
        EntityManager entityManager = entityManagerHolder.get();

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        entityManagerHolder.remove();
    }
}
