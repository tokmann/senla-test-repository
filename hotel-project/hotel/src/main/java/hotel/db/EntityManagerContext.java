package hotel.db;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;


@Component
public class EntityManagerContext {

    private final ThreadLocal<EntityManager> entityManagerHolder =
            new ThreadLocal<>();

    private final EntityManagerFactoryProvider emfProvider;

    public EntityManagerContext(EntityManagerFactoryProvider emfProvider) {
        this.emfProvider = emfProvider;
    }

    public EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerHolder.get();

        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = emfProvider.createEntityManager();
            entityManagerHolder.set(entityManager);
        }

        return entityManager;
    }

    public void clear() {
        EntityManager entityManager = entityManagerHolder.get();

        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }

        entityManagerHolder.remove();
    }
}
