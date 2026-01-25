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
            Optional<Service> existingService = serviceRepository.findByName(service.getName());
            if (existingService.isPresent()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: addService - услуга с названием {} уже существует", service.getName());
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
            Optional<Service> serviceOpt = serviceRepository.findByName(serviceName);
            if (!serviceOpt.isPresent()) {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: changeServicePrice - услуга не найдена, serviceName={}", serviceName);
                throw new ServiceNotFoundException(serviceName);
            }

            Service service = serviceOpt.get();
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

        try {
            List<Service> services = getAllServices();
            List<Service> sortedServices = services.stream()
                    .sorted(option.getComparator())
                    .collect(Collectors.toList());

            log.info("Успешно выполнена команда: getSortedServices, servicesCount={}", sortedServices.size());
            return sortedServices;
        } catch (Exception e) {
            log.error("Ошибка выполнения команды: getSortedServices, option={}", option, e);
            throw new ServiceException("Ошибка при сортировке услуг", e);
        }
    }

    /**
     * Возвращает список всех услуг.
     *
     * @return список всех услуг
     */
    @Override
    public List<Service> getAllServices() {
        log.info("Начало обработки команды: getAllServices");

        try {
            List<Service> services = serviceRepository.findAll();
            log.info("Успешно выполнена команда: getAllServices, servicesCount={}", services.size());
            return services;
        } catch (Exception e) {
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
            Optional<Service> serviceOpt = serviceRepository.findByName(name.trim());
            if (serviceOpt.isPresent()) {
                log.info("Успешно выполнена команда: findByName, service={}", serviceOpt.get());
                return serviceOpt.get();
            } else {
                log.info("Успешно выполнена команда: findByName, service не найден, name={}", name);
                return null;
            }
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

        try {
            Optional<Service> service = serviceRepository.findById(id);
            log.info("Успешно выполнена команда: getServiceById, id={}, found={}", id, service.isPresent());
            return service;
        } catch (Exception e) {
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