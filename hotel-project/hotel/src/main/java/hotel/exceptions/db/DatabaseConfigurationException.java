package hotel.exceptions.db;

import hotel.exceptions.HotelException;

/**
 * Исключение, возникающее при ошибках конфигурации базы данных.
 */
public class DatabaseConfigurationException extends HotelException {

    /**
     * Создает новое исключение с указанным сообщением.
     * @param message сообщение об ошибке
     */
    public DatabaseConfigurationException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     * @param message сообщение об ошибке
     * @param cause первоначальная причина исключения
     */
    public DatabaseConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}