package hotel.controller;

import di.Component;
import di.Inject;
import hotel.controller.interfaces.IServiceController;
import hotel.model.Service;
import hotel.service.interfaces.IServiceManager;
import hotel.view.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Component
public class ServiceController implements IServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    @Inject
    private IServiceManager serviceManager;

    @Override
    public Service addService(Service service) {
        log.info("Начало обработки команды: addService, service={}", service);
        return serviceManager.addService(service);
    }

    @Override
    public List<Service> getServices(ServiceSortOption option) {
        log.info("Начало обработки команды: getServices, option={}", option);
        return serviceManager.getSortedServices(option);
    }
}
