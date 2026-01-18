package hotel.exceptions.rooms;

public class RoomUnderMaintenanceException extends RoomException {

    public RoomUnderMaintenanceException(int roomNumber) {
        super("Комната " + roomNumber + " на обслуживании");
    }
}