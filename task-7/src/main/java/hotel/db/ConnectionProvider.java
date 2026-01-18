package task_11.db;

import di.Component;
import task_11.exceptions.db.DatabaseConfigurationException;
import task_11.exceptions.db.DatabaseConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Провайдер соединений с базой данных.
 * Реализует паттерн Singleton для управления единственным экземпляром соединения с БД.
 * Загружает конфигурацию из файла application.properties и инициализирует соединение.
 */
@Component
public class ConnectionProvider {

    private static volatile ConnectionProvider instance;
    private Connection connection;
    private static final String CONFIG_FILE = "application.properties";
    private static String DB_URL;
    private static String USER;
    private static String PASSWORD;

    /**
     * Приватный конструктор для реализации паттерна Singleton.
     * Если экземпляр уже существует, использует существующее соединение.
     * В противном случае загружает конфигурацию и инициализирует новое соединение.
     */
    public ConnectionProvider() {
        if (instance != null) {
            this.connection = instance.connection;
        } else {
            loadConfiguration();
            initConnection();
            instance = this;
        }
    }

    /**
     * Загружает параметры подключения к базе данных из файла конфигурации.
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is == null) {
                throw new DatabaseConfigurationException("Файл конфигурации " + CONFIG_FILE + " не найден в classpath");
            }
            props.load(is);

            DB_URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");

            if (DB_URL == null || USER == null || PASSWORD == null) {
                throw new DatabaseConfigurationException("Отсутствуют обязательные параметры базы данных в " + CONFIG_FILE);
            }
        } catch (IOException e) {
            throw new DatabaseConfigurationException("Ошибка загрузки файла конфигурации " + CONFIG_FILE, e);
        }
    }

    /**
     * Инициализирует соединение с базой данных.
     */
    private void initConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new DatabaseConnectionException("PostgreSQL JDBC Driver не найден. Добавьте зависимость в проект.", e);
        } catch (SQLException e) {
            throw new DatabaseConnectionException("Не удалось подключиться к базе данных: " + e.getMessage() +
                    ". Проверьте параметры в " + CONFIG_FILE, e);
        }
    }

    /**
     * Возвращает единственный экземпляр ConnectionProvider.
     * Если экземпляр еще не создан, инициализирует его.
     * @return единственный экземпляр ConnectionProvider
     */
    public static ConnectionProvider getInstance() {
        if (instance == null) {
            instance = new ConnectionProvider();
        }
        return instance;
    }

    /**
     * Возвращает текущее соединение с базой данных.
     * @return соединение с базой данных
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Закрывает соединение с базой данных.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseConfigurationException("Не удалось закрыть соединение с базой данных", e);
        }
    }
}
