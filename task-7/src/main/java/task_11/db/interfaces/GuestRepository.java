package task_11.db.interfaces;

import task_11.model.Guest;

import java.util.List;


public interface GuestRepository extends BaseRepository<Guest> {
    List<Guest> findByRoomId(long roomId);
    int count();
    void loadRoomForGuest(Guest guest);
    void loadServicesForGuest(Guest guest);
}
