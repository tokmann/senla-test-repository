package task_8.controller;

import di.Component;
import di.Inject;
import task_8.controller.interfaces.IServiceController;
import task_8.model.Service;
import task_8.service.interfaces.IServiceManager;
import task_8.view.enums.ServiceSortOption;

import java.util.List;

/**
 * Контроллер для управления услугами.
 * Отвечает за добавление и получение услуг с учётом сортировки.
 */
@Component
public class ServiceController implements IServiceController {

    @Inject
    private IServiceManager serviceManager;

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
