package task_5.service;

import task_5.model.Service;
import task_5.repository.impl.InMemoryServiceRepository;
import task_5.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер для управления услугами.
 * Отвечает за добавление, изменение и сортировку услуг.
 * Представляет бизнес-логику над репозиторием {@link InMemoryServiceRepository}.
 */
public class ServiceManager {

    private final InMemoryServiceRepository repository;

    public ServiceManager(InMemoryServiceRepository repository) {
        this.repository = repository;
    }

    /**
     * Добавляет новую услугу, если такой ещё нет в системе.
     */
    public Service addService(String name, String description, double price, LocalDate date) {
        Optional<Service> existing = repository.findByName(name);
        Service service = new Service(name, description, price, date);
        if (existing.isEmpty()) {
            repository.save(service);
            return service;
        }
        return null;
    }

    /**
     * Изменяет цену существующей услуги.
     */
    public void changeServicePrice(String serviceName, double newPrice) {
        repository.findByName(serviceName).ifPresent(service -> service.setPrice(newPrice));
    }

    /**
     * Возвращает список услуг, отсортированный по указанному критерию.
     */
    public List<Service> getSortedServices(ServiceSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Ищет услугу по имени (без учёта регистра).
     */
    public Service findByName(String name) {
        return repository.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

}