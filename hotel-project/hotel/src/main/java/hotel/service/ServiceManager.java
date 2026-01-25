package hotel.service;

import di.Component;
import di.Inject;
import hotel.db.TransactionManager;
import hotel.exceptions.ValidationException;
import hotel.exceptions.services.ServiceAlreadyExistsException;
import hotel.exceptions.services.ServiceException;
import hotel.exceptions.services.ServiceNotFoundException;
import hotel.model.Service;

import hotel.db.interfaces.ServiceRepository;
import hotel.service.interfaces.IServiceManager;
import hotel.view.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Менеджер для управления услугами отеля.
 * Содержит бизнес-логику для добавления, изменения цен и получения списка услуг.
 */
@Component
public class ServiceManager implements IServiceManager {

    private static final Logger log = LoggerFactory.getLogger(ServiceManager.class);

    @Inject
    private ServiceRepository serviceRepository;

    @Inject
    private TransactionManager transactionManager;

    /**
     * Добавляет новую услугу в систему.
     * @param service услуга для добавления
     * @return добавленная услуга
     */
    @Override
    public Service addService(Service service) {
        log.info("Начало обработки команды: addService, service={}", service);
        validateService(service);

        transactionManager.beginTransaction();
        try {
            Optional<Service> existing = serviceRepository.findByName(service.getName());
            if (existing.isPresent()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: addService - услуга уже существует, name={}", service.getName());
                throw new ServiceAlreadyExistsException(service.getName());
            }

            Service savedService = serviceRepository.save(service);
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: addService, service={}", savedService);
            return savedService;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: addService, service={}", service, e);
            throw new ServiceException("Ошибка при добавлении услуги", e);
        }
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
            log.error("Ошибка выполнения команды: changeServicePrice - пустое название услуги");
            throw new ValidationException("Название услуги не может быть пустым");
        }
        if (newPrice < 0) {
            log.error("Ошибка выполнения команды: changeServicePrice - отрицательная цена, serviceName={}, newPrice={}",
                    serviceName, newPrice);
            throw new ValidationException("Цена услуги не может быть отрицательной");
        }

        transactionManager.beginTransaction();
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new ServiceNotFoundException(serviceName));

            service.setPrice(newPrice);
            serviceRepository.save(service);
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: changeServicePrice, serviceName={}, newPrice={}", serviceName, newPrice);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: changeServicePrice, serviceName={}, newPrice={}", serviceName, newPrice, e);
            throw new ServiceException("Ошибка при изменении цены услуги", e);
        }
    }

    /**
     * Возвращает список услуг, отсортированных по указанному критерию.
     *
     * @param option критерий сортировки
     * @return отсортированный список услуг
     */
    @Override
    public List<Service> getSortedServices(ServiceSortOption option) {
        log.info("Начало обработки команды: getSortedServices, option={}", option);
        List<Service> sorted = getAllServices().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
        log.info("Успешно выполнена команда: getSortedServices, servicesCount={}", sorted.size());
        return sorted;
    }

    /**
     * Возвращает список всех услуг.
     *
     * @return список всех услуг
     */
    @Override
    public List<Service> getAllServices() {
        log.info("Начало обработки команды: getAllServices");

        transactionManager.beginTransaction();
        try {
            List<Service> services = serviceRepository.findAll();
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: getAllServices, servicesCount={}", services.size());
            return services;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: getAllServices", e);
            throw new ServiceException("Ошибка при получении списка услуг", e);
        }
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
            log.error("Ошибка выполнения команды: findByName - пустое название услуги");
            throw new ValidationException("Название услуги не может быть пустым");
        }

        try {
            Service service = serviceRepository.findByName(name.trim())
                    .orElse(null);
            if (service != null) {
                log.info("Успешно выполнена команда: findByName, service={}", service);
            } else {
                log.info("Успешно выполнена команда: findByName, service не найден, name={}", name);
            }
            return service;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: findByName, name={}", name, e);
            throw new ServiceException("Ошибка при поиске услуги по названию", e);
        }
    }

    /**
     * Возвращает услугу по её идентификатору.
     * @param id идентификатор услуги
     * @return Optional с услугой или пустой Optional, если услуга не найдена
     */
    @Override
    public Optional<Service> getServiceById(long id) {
        log.info("Начало обработки команды: getServiceById, id={}", id);

        transactionManager.beginTransaction();
        try {
            Optional<Service> service = serviceRepository.findById(id);
            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: getServiceById, id={}, found={}", id, service.isPresent());
            return service;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: getServiceById, id={}", id, e);
            throw new ServiceNotFoundException("Ошибка при поиске услуги по ID");
        }
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