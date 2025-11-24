package task_7.exceptions.guests;

public class GuestNotCheckedInException extends GuestException {

    public GuestNotCheckedInException(long guestId) {
        super("Гость с ID " + guestId + " не заселен в комнату");
    }
}
