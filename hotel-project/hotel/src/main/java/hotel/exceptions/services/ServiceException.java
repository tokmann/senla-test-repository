package hotel.exceptions.services;

import hotel.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над услугами.
 */
public class ServiceException extends HotelException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Exception e) {
        super(message, e);
    }
}
