package hotel.db;

import jakarta.annotation.PostConstruct;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Component("liquibaseRunner")
public class LiquibaseRunner {

    private static final Logger log = LoggerFactory.getLogger(EntityManagerFactoryProvider.class);

    @Value("${db.url}")
    private String dbUrl;

    @Value("${db.user}")
    private String dbUser;

    @Value("${db.password}")
    private String dbPassword;

    @PostConstruct
    public void runMigrations() throws ClassNotFoundException {
        log.info("Запуск Liquibase миграций...");

        Class.forName("org.postgresql.Driver");

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update(new Contexts(), new LabelExpression());
            log.info("Liquibase миграции успешно применены");
        } catch (SQLException | LiquibaseException e) {
            log.error("Ошибка выполнения миграций Liquibase", e);
            throw new RuntimeException("Не удалось применить миграции БД", e);
        }
    }
}
