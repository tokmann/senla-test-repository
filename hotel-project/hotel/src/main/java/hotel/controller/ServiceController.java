package hotel.controller;

import hotel.controller.interfaces.IServiceController;
import hotel.model.Service;
import hotel.service.interfaces.IServiceManager;
import hotel.view.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Контроллер для управления услугами отеля.
 * Делегирует бизнес-операции в {@link IServiceManager}.
 */
@Component
public class ServiceController implements IServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    private final IServiceManager serviceManager;

    public ServiceController(IServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    /**
     * Добавляет новую услугу.
     * @param service объект услуги
     * @return добавленная услуга
     */
    @Override
    public Service addService(Service service) {
        log.info("Начало обработки команды: addService, service={}", service);
        return serviceManager.addService(service);
    }

    /**
     * Возвращает список услуг с сортировкой.
     * @param option параметр сортировки
     * @return список услуг
     */
    @Override
    public List<Service> getServices(ServiceSortOption option) {
        log.info("Начало обработки команды: getServices, option={}", option);
        return serviceManager.getSortedServices(option);
    }
}
