package task_11.exceptions.dao;

import task_11.exceptions.HotelException;

public class DaoException extends HotelException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message + ", " + cause.toString());
    }
}
