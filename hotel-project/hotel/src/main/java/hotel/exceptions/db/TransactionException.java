package hotel.exceptions.db;

import hotel.exceptions.HotelException;

/**
 * Исключение, возникающее при ошибках работы с транзакциями базы данных.
 */
public class TransactionException extends HotelException {

    /**
     * Создает новое исключение с указанным сообщением.
     * @param message сообщение об ошибке
     */
    public TransactionException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение с указанным сообщением и причиной.
     * @param message сообщение об ошибке
     * @param cause первоначальная причина исключения
     */
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
