package task_5.view.enums;

import task_5.model.Guest;
import task_5.service.GuestManager;

import java.util.Comparator;

/**
 * Опции сортировки гостей.
 * Используется для динамической сортировки списка {@link Guest}
 * по выбранному критерию в слое {@link GuestManager}.
 */
public enum GuestSortOption {

    /** Сортировка по алфавиту (по полному имени). */
    ALPHABET("Алфавит", Comparator.comparing(Guest::getFullName)),

    /** Сортировка по дате освобождения номера. */
    CHECKOUT_DATE("Дата освобождения номера", Comparator.comparing(guest -> guest.getGuestRoom().getCheckOutDate()));

    private final String description;
    private final Comparator<Guest> comparator;

    GuestSortOption(String description, Comparator<Guest> comparator) {
        this.description = description;
        this.comparator = comparator;
    }

    public String getDescription() {
        return description;
    }

    public Comparator<Guest> getComparator() {
        return comparator;
    }

    /**
     * Возвращает вариант сортировки по текстовому описанию.
     */
    public static GuestSortOption fromDescription(String input) {
        for (GuestSortOption option : values()) {
            if (option.description.equalsIgnoreCase(input.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Неизвестный критерий сортировки: " + input);
    }
}
