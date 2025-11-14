package task_5.repository.impl;

import task_5.model.Room;
import task_5.repository.RoomRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Репозиторий комнат.
 * Отвечает за хранение и поиск комнат по номеру.
 */
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<Long, Room> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @Override
    public Room save(Room room) {
        if (room.getId() == 0) {
            long newId = idGen.getAndIncrement();
            setId(room, newId);
        }

        storage.put(room.getId(), room);
        return room;
    }

    @Override
    public void delete(Room room) {
        storage.remove(room.getId());
    }

    @Override
    public Optional<Room> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Room> findByNumber(int number) {
        return storage.values().stream()
                .filter(r -> r.getNumber() == number)
                .findFirst();
    }

    private void setId(Room room, long id) {
        try {
            Field f = Room.class.getDeclaredField("id");
            f.setAccessible(true);
            f.setLong(room, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
