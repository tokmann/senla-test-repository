package hotel.exceptions.guests;

import hotel.exceptions.HotelException;

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
