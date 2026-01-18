package task_11.view.enums;

import task_11.model.Room;
import task_11.service.RoomManager;

import java.util.Comparator;

/**
 * Опции сортировки для списка номеров {@link Room}.
 * Используется в {@link RoomManager} при получении
 * отсортированных коллекций номеров.
 */
public enum RoomSortOption {

    BY_NUMBER(Comparator.comparingInt(Room::getNumber)),
    BY_PRICE(Comparator.comparingDouble(Room::getPrice)),
    BY_STARS(Comparator.comparingInt(Room::getStars).reversed());

    private final Comparator<Room> comparator;

    RoomSortOption(Comparator<Room> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Room> getComparator() {
        return comparator;
    }
}
