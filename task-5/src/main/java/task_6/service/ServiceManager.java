package task_6.service;

import task_6.model.Service;
import task_6.repository.impl.InMemoryServiceRepository;
import task_6.repository.interfaces.ServiceRepository;
import task_6.view.enums.ServiceSortOption;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер для управления услугами.
 * Отвечает за добавление, изменение и сортировку услуг.
 * Представляет бизнес-логику над репозиторием {@link InMemoryServiceRepository}.
 */
public class ServiceManager {

    private final ServiceRepository repository;

    public ServiceManager(ServiceRepository repository) {
        this.repository = repository;
    }

    /**
     * Добавляет новую услугу, если такой ещё нет в системе.
     */
    public Service addService(Service service) {
        Optional<Service> existing = repository.findByName(service.getName());
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

    public List<Service> getAllServices() {
        return repository.findAll();
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

    public Service getServiceById(long id) {
        return repository.findAll().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }
}