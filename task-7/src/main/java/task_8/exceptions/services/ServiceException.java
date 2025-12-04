package task_8.exceptions.services;

import task_8.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над услугами.
 */
public class ServiceException extends HotelException {

    public ServiceException(String message) {
        super(message);
    }
}
