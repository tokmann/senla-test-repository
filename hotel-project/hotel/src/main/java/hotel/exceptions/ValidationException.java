package hotel.exceptions;

/**
 * Исключения, связанные с валидацией данных.
 */
public class ValidationException extends HotelException {

    public ValidationException(String message) {
        super(message);
    }
}
