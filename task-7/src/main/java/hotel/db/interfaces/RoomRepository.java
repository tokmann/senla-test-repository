package task_11.db.interfaces;

import task_11.model.Room;

import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room> {
    Optional<Room> findByNumber(int number);
    int countFree();
}
