package task_5.view.enums;

import task_5.model.Service;
import task_5.service.ServiceManager;

import java.util.Comparator;

/**
 * Опции сортировки услуг {@link Service}.
 * Применяется в {@link ServiceManager} для отображения списка
 * услуг в удобном для пользователя порядке.
 */
public enum ServiceSortOption {

    /** Сортировка по цене. */
    PRICE("Цена", Comparator.comparing(Service::getPrice)),

    /** Сортировка по названию. */
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

    /**
     * Возвращает вариант сортировки по описанию.
     *
     */
    public static ServiceSortOption fromDescription(String input) {
        for (ServiceSortOption option : values()) {
            if (option.description.equalsIgnoreCase(input.trim())) {
                return option;
            }
        }
        throw new IllegalArgumentException("Неизвестный критерий сортировки: " + input);
    }
}
