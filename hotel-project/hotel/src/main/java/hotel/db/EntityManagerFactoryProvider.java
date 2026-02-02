package hotel.db;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EntityManagerFactoryProvider {

    private static final Logger log =
            LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    private final EntityManagerFactory entityManagerFactory;

    public EntityManagerFactoryProvider(
            @Value("${db.url}") String url,
            @Value("${db.user}") String user,
            @Value("${db.password}") String password
    ) {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("jakarta.persistence.jdbc.url", url);
        properties.put("jakarta.persistence.jdbc.user", user);
        properties.put("jakarta.persistence.jdbc.password", password);
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.connection.pool_size", "5");

        this.entityManagerFactory = Persistence.
                createEntityManagerFactory("hotel-persistence-unit", properties);

        log.info("EntityManagerFactory успешно создан");
    }

    public EntityManager createEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    @PreDestroy
    public void close() {
        if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            log.info("EntityManagerFactory закрыт");
        }
    }
}
