package task_5.model.repository;

import task_5.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий комнат.
 * Отвечает за хранение и поиск комнат по номеру.
 */
public class RoomRepository {

    private final List<Room> rooms = new ArrayList<>();

    public void save(Room room) {
        rooms.add(room);
    }

    public void delete(Room room) {
        rooms.remove(room);
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms);
    }

    /** Поиск комнаты по её номеру */
    public Optional<Room> findByNumber(int number) {
        return rooms.stream().filter(r -> r.getNumber() == number).findFirst();
    }
}
