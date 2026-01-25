package hotel.db.interfaces;

import hotel.model.Room;

import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room> {
    Optional<Room> findByNumber(int number);
    int countFree();
}
