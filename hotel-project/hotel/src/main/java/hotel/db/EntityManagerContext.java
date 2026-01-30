package hotel.db;

import di.Component;
import di.Inject;
import jakarta.persistence.EntityManager;


@Component
public class EntityManagerContext {

    private static final ThreadLocal<EntityManager> entityManagerHolder = new ThreadLocal<>();

    @Inject
    private EntityManagerFactoryProvider entityManagerFactoryProvider;

    public EntityManager getEntityManager() {
        EntityManager entityManager = entityManagerHolder.get();
        if (entityManager == null || !entityManager.isOpen()) {
            entityManager = entityManagerFactoryProvider.createEntityManager();
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
