package task_7.exceptions.services;

import task_7.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над услугами.
 */
public class ServiceException extends HotelException {

    public ServiceException(String message) {
        super(message);
    }
}
