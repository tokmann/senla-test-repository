package task_5.model.repository;

import task_5.model.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий услуг.
 * Позволяет искать услуги по имени и управлять их списком.
 */
public class ServiceRepository {

    private final List<Service> services = new ArrayList<>();

    public void save(Service service) {
        services.add(service);
    }

    public void delete(Service service) {
        services.remove(service);
    }

    public List<Service> findAll() {
        return new ArrayList<>(services);
    }

    /** Поиск услуги по названию (без учёта регистра) */
    public Optional<Service> findByName(String name) {
        return services.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
    }
}
