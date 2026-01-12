package task_11.exceptions.guests;

public class GuestNotFoundException extends GuestException {

    public GuestNotFoundException(long guestId) {
        super("Гость с ID " + guestId + " не найден");
    }

    public GuestNotFoundException(String guestName) {
        super("Гость '" + guestName + "' не найден");
    }
}
