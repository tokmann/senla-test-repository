package task_8.view.enums;

import task_8.model.Room;
import task_8.service.RoomManager;

import java.util.Comparator;

/**
 * Опции сортировки для списка номеров {@link Room}.
 * Используется в {@link RoomManager} при получении
 * отсортированных коллекций номеров.
 */
public enum RoomSortOption {

    /** Сортировка по цене за ночь. */
    PRICE("Цена",Comparator.comparingDouble(Room::getPrice)),

    /** Сортировка по вместимости номера. */
    CAPACITY("Вместимость", Comparator.comparingInt(Room::getCapacity)),

    /** Сортировка по количеству звёзд (уровню комфорта). */
    STARS("Звезды", Comparator.comparing(Room::getStars));

    private final String description;
    private final Comparator<Room> comparator;

    RoomSortOption(String description, Comparator<Room> comparator) {
        this.description = description;
        this.comparator = comparator;
    }

    public Comparator<Room> getComparator() {
        return comparator;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Возвращает критерий сортировки по описанию.
     */
    public static RoomSortOption fromDescription(String input) {
        for (RoomSortOption option : values()) {
            if (option.description.equalsIgnoreCase(input.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Неизвестный критерий сортировки: " + input);
    }
}
