package task_8.exceptions.guests;

import task_8.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над гостями.
 */
public class GuestException extends HotelException {
    public GuestException(String message) {
        super(message);
    }
}
