package hotel.service;

import hotel.exceptions.ValidationException;
import hotel.exceptions.services.ServiceAlreadyExistsException;
import hotel.exceptions.services.ServiceNotFoundException;
import hotel.model.Service;

import hotel.db.interfaces.ServiceRepository;
import hotel.service.interfaces.IServiceManager;
import hotel.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер для управления услугами отеля.
 * Содержит бизнес-логику для добавления, изменения цен и получения списка услуг.
 */
@Transactional
@org.springframework.stereotype.Service
public class ServiceManager implements IServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    private final ServiceRepository serviceRepository;

    public ServiceManager(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    /**
     * Добавляет новую услугу в систему.
     * @param service услуга для добавления
     * @return добавленная услуга
     */
    @Override
    public Service addService(Service service) {
        log.info("Начало обработки команды: addService, service={}", service);
        validateService(service);

        Service existingService = serviceRepository.findByName(service.getName());
        if (existingService != null) {
            log.error("Ошибка выполнения команды: addService - услуга с названием {} уже существует", service.getName());
            throw new ServiceAlreadyExistsException(service.getName());
        }

        Service savedService = serviceRepository.save(service);
        log.info("Успешно выполнена команда: addService, service={}", savedService);
        return savedService;
    }

    /**
     * Изменяет цену услуги по её названию.
     * @param serviceName название услуги
     * @param newPrice новая цена
     */
    @Override
    public void changeServicePrice(String serviceName, double newPrice) {
        log.info("Начало обработки команды: changeServicePrice, serviceName={}, newPrice={}", serviceName, newPrice);

        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }
        if (newPrice < 0) {
            throw new ValidationException("Цена услуги не может быть отрицательной");
        }

        Service service = serviceRepository.findByName(serviceName);
        if (service == null) {
            throw new ServiceNotFoundException(serviceName);
        }

        service.setPrice(newPrice);
        serviceRepository.save(service);

        log.info("Успешно выполнена команда: changeServicePrice, serviceName={}, newPrice={}", serviceName, newPrice);
    }

    /**
     * Возвращает список услуг, отсортированных по указанному критерию.
     * @param option критерий сортировки
     * @return отсортированный список услуг
     */
    @Override
    public List<Service> getSortedServices(ServiceSortOption option) {
        log.info("Начало обработки команды: getSortedServices, option={}", option);
        List<Service> services = getAllServices();
        return services.stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех услуг.
     * @return список всех услуг
     */
    @Override
    public List<Service> getAllServices() {
        log.info("Начало обработки команды: getAllServices");
        return serviceRepository.findAll();
    }

    /**
     * Находит услугу по названию.
     * @param name название услуги
     * @return услуга или null, если не найдена
     */
    @Override
    public Service findByName(String name) {
        log.info("Начало обработки команды: findByName, name={}", name);
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }

        return serviceRepository.findByName(name.trim());
    }

    /**
     * Возвращает услугу по её идентификатору.
     * @param id идентификатор услуги
     * @return Optional с услугой или пустой Optional, если услуга не найдена
     */
    @Override
    public Service getServiceById(long id) {
        log.info("Начало обработки команды: getServiceById, id={}", id);
        Service service = serviceRepository.findById(id);
        if (service == null) {
            throw new ServiceNotFoundException(id);
        }
        return service;
    }

    /**
     * Валидирует данные услуги перед сохранением.
     * @param service услуга для валидации
     */
    private void validateService(Service service) {
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }
        if (service.getPrice() < 0) {
            throw new ValidationException("Цена услуги не может быть отрицательной");
        }
    }
}