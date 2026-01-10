package task_11.controller.interfaces;

import task_11.model.Service;
import task_11.view.enums.ServiceSortOption;

import java.util.List;

/**
 * Интерфейс контроллера для управления услугами.
 * Отвечает за добавление и получение услуг с учётом сортировки.
 */
public interface IServiceController {

    /** Добавление новой услуги */
    Service addService(Service service);

    /** Получение списка услуг по выбранному критерию сортировки */
    List<Service> getServices(ServiceSortOption option);
}
