package task_6.repository.interfaces;

import task_6.model.Room;

import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room> {
    Optional<Room> findByNumber(int number);
}
