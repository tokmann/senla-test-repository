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

import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер для управления услугами отеля.
 * Содержит бизнес-логику для добавления, изменения цен и получения списка услуг.
 */
@Component
public class ServiceManager implements IServiceManager {

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
        validateService(service);

        transactionManager.beginTransaction();
        try {
            Optional<Service> existing = serviceRepository.findByName(service.getName());
            if (existing.isPresent()) {
                transactionManager.rollbackTransaction();
                throw new ServiceAlreadyExistsException(service.getName());
            }

            Service savedService = serviceRepository.save(service);
            transactionManager.commitTransaction();
            return savedService;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }
        if (newPrice < 0) {
            throw new ValidationException("Цена услуги не может быть отрицательной");
        }

        transactionManager.beginTransaction();
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new ServiceNotFoundException(serviceName));

            service.setPrice(newPrice);
            serviceRepository.save(service);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
        return getAllServices().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех услуг.
     *
     * @return список всех услуг
     */
    @Override
    public List<Service> getAllServices() {
        transactionManager.beginTransaction();
        try {
            List<Service> services = serviceRepository.findAll();
            transactionManager.commitTransaction();
            return services;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }

        try {
            Service service = serviceRepository.findByName(name.trim())
                    .orElse(null);
            if (service != null) {
            } else {
            }
            return service;
        } catch (Exception e) {
            e.printStackTrace();
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
        transactionManager.beginTransaction();
        try {
            Optional<Service> service = serviceRepository.findById(id);
            transactionManager.commitTransaction();
            return service;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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