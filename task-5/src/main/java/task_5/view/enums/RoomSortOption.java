package task_5.view.enums;

import task_5.model.Room;

import java.util.Comparator;

public enum RoomSortOption {

    PRICE("Цена",Comparator.comparingDouble(Room::getPrice)),
    CAPACITY("Вместимость", Comparator.comparingInt(Room::getCapacity)),
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

    public static RoomSortOption fromDescription(String input) {
        for (RoomSortOption option : values()) {
            if (option.description.equalsIgnoreCase(input.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Неизвестный критерий сортировки: " + input);
    }
}
