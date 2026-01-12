package task_11.exceptions.guests;

public class GuestAlreadyCheckedInException extends GuestException {

    public GuestAlreadyCheckedInException(long guestId) {
        super("Гость с ID " + guestId + " уже заселен в комнату");
    }
}
