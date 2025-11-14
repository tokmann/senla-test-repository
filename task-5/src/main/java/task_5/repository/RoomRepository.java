package task_5.repository;

import task_5.model.Room;

import java.util.Optional;

public interface RoomRepository extends BaseRepository<Room> {
    Optional<Room> findByNumber(int number);
}
