package task_6.exceptions.services;

import task_6.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над услугами.
 */
public class ServiceException extends HotelException {

    public ServiceException(String message) {
        super(message);
    }
}
