package task_11.view.enums;

import task_11.model.Guest;
import task_11.service.GuestManager;

import java.util.Comparator;

/**
 * Опции сортировки гостей.
 * Используется для динамической сортировки списка {@link Guest}
 * по выбранному критерию в слое {@link GuestManager}.
 */
public enum GuestSortOption {

    BY_NAME((g1, g2) -> g1.getFullName().compareTo(g2.getFullName())),
    BY_AGE(Comparator.comparingInt(Guest::getAge)),
    BY_ROOM_NUMBER((g1, g2) -> {
        Integer room1 = (g1.getRoom() != null) ? g1.getRoom().getNumber() : null;
        Integer room2 = (g2.getRoom() != null) ? g2.getRoom().getNumber() : null;
        return Comparator.nullsLast(Integer::compare).compare(room1, room2);
    });

    private final Comparator<Guest> comparator;

    GuestSortOption(Comparator<Guest> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Guest> getComparator() {
        return comparator;
    }
}
