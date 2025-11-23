package task_7.exceptions.rooms;

public class RoomOccupiedException extends RoomException {

    public RoomOccupiedException(int roomNumber) {
        super("Комната " + roomNumber + " занята, операция невозможна");
    }
}