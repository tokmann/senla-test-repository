package task_5.view.enums;

import task_5.model.Service;

import java.util.Comparator;

public enum ServiceSortOption {

    PRICE("Цена", Comparator.comparing(Service::getPrice)),
    NAME("Название", Comparator.comparing(Service::getName));

    private final String description;
    private final Comparator<Service> comparator;

    ServiceSortOption(String description, Comparator<Service> comparator) {
        this.description = description;
        this.comparator = comparator;
    }

    public String getDescription() {
        return description;
    }

    public Comparator<Service> getComparator() {
        return comparator;
    }

    public static ServiceSortOption fromDescription(String input) {
        for (ServiceSortOption option : values()) {
            if (option.description.equalsIgnoreCase(input.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Неизвестный критерий сортировки: " + input);
    }
}
