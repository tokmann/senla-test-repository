package task_11.exceptions.rooms;

import task_11.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над комнатами.
 */
public class RoomException extends HotelException {

    public RoomException(String message) {
        super(message);
    }

    public RoomException(String message, Exception e) {
        super(message, e);
    }
}
