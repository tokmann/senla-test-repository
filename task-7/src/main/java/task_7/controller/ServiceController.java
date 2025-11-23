package task_7.controller;

import task_7.model.Service;
import task_7.service.ServiceManager;
import task_7.view.enums.ServiceSortOption;

import java.util.List;

/**
 * Контроллер для управления услугами.
 * Отвечает за добавление и получение услуг с учётом сортировки.
 */
public class ServiceController {
    private final ServiceManager serviceManager;

    public ServiceController(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /** Добавление новой услуги */
    public Service addService(Service service) {
        return serviceManager.addService(service);
    }

    /** Получение списка услуг по выбранному критерию сортировки */
    public List<Service> getServices(ServiceSortOption option) {
        return serviceManager.getSortedServices(option);
    }
}
