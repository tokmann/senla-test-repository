package hotel.db;

import di.Component;
import hotel.util.ConfigurationLoader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component
public class EntityManagerFactoryProvider {

    private final static Logger log = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    private EntityManagerFactory entityManagerFactory;
    private boolean initialized = false;

    public synchronized void initialize() {
        if (initialized) return;

        try {
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
            properties.put("jakarta.persistence.jdbc.url", ConfigurationLoader.getProperty("db.url"));
            properties.put("jakarta.persistence.jdbc.user", ConfigurationLoader.getProperty("db.user"));
            properties.put("jakarta.persistence.jdbc.password", ConfigurationLoader.getProperty("db.password"));
            properties.put("hibernate.hbm2ddl.auto", "update");
            properties.put("hibernate.show_sql", "true");
            properties.put("hibernate.format_sql", "true");
            properties.put("hibernate.connection.pool_size", "5");

            entityManagerFactory = Persistence.createEntityManagerFactory("hotel-persistence-unit", properties);
            initialized = true;
            log.info("Успешно создан Создан EntityManagerFactory");
        } catch (Exception e) {
            log.error("Не удалось создать EntityManagerFactory", e);
            throw new RuntimeException("Не удалось создать EntityManagerFactory", e);
        }
    }

    public EntityManager createEntityManager() {
        if (!initialized) {
            initialize();
        }
        return entityManagerFactory.createEntityManager();
    }

    public void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            initialized = false;
        }
    }
}
