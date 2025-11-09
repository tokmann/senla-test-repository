package task_5.view.enums;

import task_5.model.Guest;

import java.util.Comparator;

public enum GuestSortOption {

    ALPHABET("Алфавит", Comparator.comparing(Guest::getFullName)),
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

    public static GuestSortOption fromDescription(String input) {
        for (GuestSortOption option : values()) {
            if (option.description.equalsIgnoreCase(input.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Неизвестный критерий сортировки: " + input);
    }
}
