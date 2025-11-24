package task_7.controller;

import task_7.controller.interfaces.IServiceController;
import task_7.model.Service;
import task_7.service.ServiceManager;
import task_7.service.interfaces.IServiceManager;
import task_7.view.enums.ServiceSortOption;

import java.util.List;

/**
 * Контроллер для управления услугами.
 * Отвечает за добавление и получение услуг с учётом сортировки.
 */
public class ServiceController implements IServiceController {
    private final IServiceManager serviceManager;

    public ServiceController(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /** Добавление новой услуги */
    @Override
    public Service addService(Service service) {
        return serviceManager.addService(service);
    }

    /** Получение списка услуг по выбранному критерию сортировки */
    @Override
    public List<Service> getServices(ServiceSortOption option) {
        return serviceManager.getSortedServices(option);
    }
}
