package task_6.exceptions;

/**
 * Базовое исключение для всех ошибок в приложении отеля.
 */
public class HotelException extends RuntimeException{

    public HotelException(String message) {
        super(message);
    }
}
