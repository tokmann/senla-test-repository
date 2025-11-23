package task_7.repository.interfaces;

import task_7.model.Room;

import java.util.Optional;

/**
 * Репозиторий для работы с номерами отеля.
 * Предоставляет метод для поиска номеров.
 */
public interface RoomRepository extends BaseRepository<Room> {

    /**
     * Находит номер по физическому номеру комнаты.
     * @param number физический номер комнаты
     * @return Optional с найденным номером или empty если не найден
     */
    Optional<Room> findByNumber(int number);
}
