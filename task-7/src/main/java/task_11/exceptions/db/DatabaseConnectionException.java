package task_11.exceptions.db;

import task_11.exceptions.HotelException;

/**
 * Исключение, возникающее при ошибках подключения к базе данных.
 */
public class DatabaseConnectionException extends HotelException {

    /**
     * Создает новое исключение с указанным сообщением.
     * @param message сообщение об ошибке
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     * @param message сообщение об ошибке
     * @param cause первоначальная причина исключения
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
