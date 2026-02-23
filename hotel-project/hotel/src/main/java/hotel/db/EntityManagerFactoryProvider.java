package hotel.db;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Провайдер фабрики менеджеров сущностей для JPA.
 * Создаёт и настраивает EntityManagerFactory при запуске приложения.
 */
@Component
@DependsOn("liquibaseRunner")
public class EntityManagerFactoryProvider {

    private static final Logger log = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

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
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.connection.pool_size", "5");

        this.entityManagerFactory = Persistence.
                createEntityManagerFactory("hotel-persistence-unit", properties);

        log.info("EntityManagerFactory успешно создан");
    }

    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * Закрывает фабрику менеджеров сущностей при уничтожении компонента.
     */
    @PreDestroy
    public void close() {
        if (entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
            log.info("EntityManagerFactory закрыт");
        }
    }
}
