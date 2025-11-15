package task_6.exceptions.guests;

import task_6.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над гостями.
 */
public class GuestException extends HotelException {
    public GuestException(String message) {
        super(message);
    }
}
