package task_11.view.enums;

import task_11.model.Service;
import task_11.service.ServiceManager;

import java.util.Comparator;

/**
 * Опции сортировки услуг {@link Service}.
 * Применяется в {@link ServiceManager} для отображения списка
 * услуг в удобном для пользователя порядке.
 */
public enum ServiceSortOption {

    BY_NAME((s1, s2) -> s1.getName().compareTo(s2.getName())),
    BY_PRICE(Comparator.comparingDouble(Service::getPrice));

    private final Comparator<Service> comparator;

    ServiceSortOption(Comparator<Service> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Service> getComparator() {
        return comparator;
    }
}
