package hotel.db.interfaces;

import hotel.model.Room;


public interface RoomRepository extends BaseRepository<Room> {
    Room findByNumber(int number);
    int countFree();
}