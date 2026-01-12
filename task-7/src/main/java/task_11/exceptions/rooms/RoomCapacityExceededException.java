package task_11.exceptions.rooms;

public class RoomCapacityExceededException extends RoomException {

    public RoomCapacityExceededException(int roomNumber, int capacity, int requested) {
        super("Комната " + roomNumber + " вмещает только " + capacity +
                " гостей, попытка заселить " + requested);
    }
}
