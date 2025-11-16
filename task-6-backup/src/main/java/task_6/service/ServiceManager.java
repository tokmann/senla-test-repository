package task_6.service;

import task_6.exceptions.ValidationException;
import task_6.exceptions.services.ServiceAlreadyExistsException;
import task_6.exceptions.services.ServiceNotFoundException;
import task_6.model.Service;
import task_6.repository.InMemoryServiceRepository;
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
     * Добавляет новую услугу в систему.
     * Услуга добавляется только если услуги с таким названием еще нет.
     * @param service услуга для добавления
     * @return добавленная услуга или null если услуга с таким названием уже существует
     */
    public Service addService(Service service) {
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }
        if (service.getPrice() < 0) {
            throw new ValidationException("Цена услуги не может быть отрицательной");
        }

        Optional<Service> existing = repository.findByName(service.getName());
        if (existing.isPresent()) {
            throw new ServiceAlreadyExistsException(service.getName());
        }

        repository.save(service);
        return service;
    }

    /**
     * Изменяет цену существующей услуги.
     * @param serviceName название услуги
     * @param newPrice новая цена услуги
     */
    public void changeServicePrice(String serviceName, double newPrice) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }
        if (newPrice < 0) {
            throw new ValidationException("Цена услуги не может быть отрицательной");
        }

        Service service = repository.findByName(serviceName)
                .orElseThrow(() -> new ServiceNotFoundException(serviceName));

        service.setPrice(newPrice);
    }

    /**
     * Возвращает список услуг, отсортированный по указанному критерию.
     * @param option критерий сортировки услуг
     * @return отсортированный список услуг
     */
    public List<Service> getSortedServices(ServiceSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает все услуги системы.
     * @return список всех услуг
     */
    public List<Service> getAllServices() {
        return repository.findAll();
    }

    /**
     * Ищет услугу по названию (без учёта регистра).
     * Убирает пробелы в начале и конце названия перед поиском.
     * @param name название услуги для поиска
     * @return найденная услуга или null если не найдена
     */
    public Service findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }

        return repository.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Находит услугу по идентификатору.
     * @param id идентификатор услуги
     * @return найденная услуга или null если не найдена
     */
    public Optional<Service> getServiceById(long id) {
        return repository.findById(id);
    }
}