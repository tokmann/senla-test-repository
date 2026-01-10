package task_11.db;

import config.AnnotationConfigurationLoader;
import config.ConfigProperty;
import config.ConfigType;
import di.Component;
import di.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class ConnectionProvider {

    private static volatile ConnectionProvider instance;
    private Connection connection;
    private static final String CONFIG_FILE = "application.properties";
    private static String DB_URL;
    private static String USER;
    private static String PASSWORD;

    public ConnectionProvider() {
        if (instance != null) {
            this.connection = instance.connection;
        } else {
            loadConfiguration();
            initConnection();
            instance = this;
        }
    }

    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new RuntimeException("Файл конфигурации " + CONFIG_FILE + " не найден в classpath");
            }
            props.load(is);

            DB_URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            if (DB_URL == null || USER == null || PASSWORD == null) {
                throw new RuntimeException("Отсутствуют обязательные параметры базы данных в " + CONFIG_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла конфигурации " + CONFIG_FILE, e);
        }
    }

    private void initConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver не найден. Добавьте зависимость в проект.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось подключиться к базе данных: " + e.getMessage() +
                    ". Проверьте параметры в " + CONFIG_FILE, e);
        }
    }

    public static ConnectionProvider getInstance() {
        if (instance == null) {
            instance = new ConnectionProvider();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Не удалось закрыть соединение с базой данных", e);
        }
    }
}
