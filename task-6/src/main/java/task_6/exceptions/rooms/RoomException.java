package task_6.exceptions.rooms;

import task_6.exceptions.HotelException;

/**
 * Исключения, связанные с операциями над комнатами.
 */
public class RoomException extends HotelException {

    public RoomException(String message) {
        super(message);
    }
}
