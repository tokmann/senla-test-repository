package task_11.exceptions.rooms;

public class RoomNotFoundException extends RoomException {

    public RoomNotFoundException(int roomNumber) {
        super("Комната с номером " + roomNumber + " не найдена");
    }

    public RoomNotFoundException(long roomId) {
        super("Комната с ID " + roomId + " не найдена");
    }

    public RoomNotFoundException(long roomId, Exception e) {
        super("Комната с ID " + roomId + " не найдена", e);
    }
}