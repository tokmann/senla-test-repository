package task_11.repository;

import di.Component;
import task_11.model.Room;
import task_11.repository.interfaces.RoomRepository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory реализация репозитория комнат.
 * Хранит данные в HashMap с поддержкой поиска по номеру комнаты.
 */
@Component
public class InMemoryRoomRepository implements RoomRepository {

    private final Map<Long, Room> storage = new HashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    /**
     * Сохраняет комнату в хранилище.
     * Если комната новая (id = 0), генерирует уникальный ID.
     * @param room комната для сохранения
     * @return сохраненная комната с установленным ID
     */
    @Override
    public Room save(Room room) {
        if (room.getId() == 0) {
            long newId = idGen.getAndIncrement();
            setId(room, newId);
        }

        storage.put(room.getId(), room);
        return room;
    }

    /**
     * Удаляет комнату из хранилища.
     * @param room комната для удаления
     */
    @Override
    public void delete(Room room) {
        storage.remove(room.getId());
    }

    /**
     * Находит комнату по идентификатору.
     * @param id идентификатор комнаты
     * @return Optional с найденной комнатой или empty если не найдена
     */
    @Override
    public Optional<Room> findById(long id) {
        return Optional.ofNullable(storage.get(id));
    }

    /**
     * Возвращает все комнаты из хранилища.
     * @return список всех комнат
     */
    @Override
    public List<Room> findAll() {
        return new ArrayList<>(storage.values());
    }

    /**
     * Находит комнату по физическому номеру.
     * @param number физический номер комнаты
     * @return Optional с найденной комнатой или empty если не найдена
     */
    @Override
    public Optional<Room> findByNumber(int number) {
        return storage.values().stream()
                .filter(r -> r.getNumber() == number)
                .findFirst();
    }

    /**
     * Устанавливает ID комнате через reflection.
     * @param room комната для установки ID
     * @param id новый идентификатор
     */
    private void setId(Room room, long id) {
        try {
            Field f = Room.class.getDeclaredField("id");
            f.setAccessible(true);
            f.setLong(room, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод для синхронизации Id после десериализации
     * */
    public void syncIdGen() {
        long maxId = storage.keySet().stream().mapToLong(Long::longValue).max().orElse(0L);
        idGen.set(maxId + 1);
    }
}
