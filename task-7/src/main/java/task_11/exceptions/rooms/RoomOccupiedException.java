package task_8.exceptions.rooms;

public class RoomOccupiedException extends RoomException {

    public RoomOccupiedException(int roomNumber) {
        super("Комната " + roomNumber + " занята, операция невозможна");
    }
}