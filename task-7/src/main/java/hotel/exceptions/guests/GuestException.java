package task_11.exceptions.guests;

import task_11.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над гостями.
 */
public class GuestException extends HotelException {
    public GuestException(String message) {
        super(message);
    }

    public GuestException(String message, Exception e) {
        super(message, e);
    }
}
