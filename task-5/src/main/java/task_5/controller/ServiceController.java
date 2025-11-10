package task_5.controller;

import task_5.model.Service;
import task_5.service.ServiceManager;
import task_5.view.enums.ServiceSortOption;

import java.time.LocalDate;
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
    public Service addService(String name, String desc, double price, LocalDate date) {
        return serviceManager.addService(name, desc, price, date);
    }

    /** Получение списка услуг по выбранному критерию сортировки */
    public List<Service> getServices(ServiceSortOption option) {
        return serviceManager.getSortedServices(option);
    }
}
