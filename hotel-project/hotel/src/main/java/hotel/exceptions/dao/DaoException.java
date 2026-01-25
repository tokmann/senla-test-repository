package hotel.exceptions.dao;

import hotel.exceptions.HotelException;

public class DaoException extends HotelException {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message + ", " + cause.toString());
    }
}
