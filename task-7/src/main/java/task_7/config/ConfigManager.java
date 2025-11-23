package task_7.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Менеджер конфигурации. Загружает настройки из config.properties.
 * Используется для динамической конфигурации приложения.
 */
public class ConfigManager {

    private static ConfigManager instance;
    private final Properties properties = new Properties();

    private ConfigManager() {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            properties.setProperty("room.status.change.enabled", "true");
            properties.setProperty("room.history.size", "5");
        }
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public boolean isRoomStatusChangeEnabled() {
        return Boolean.parseBoolean(properties.getProperty("room.status.change.enabled", "true"));
    }

    public int getRoomHistorySize() {
        return Integer.parseInt(properties.getProperty("room.history.size", "5"));
    }
}
