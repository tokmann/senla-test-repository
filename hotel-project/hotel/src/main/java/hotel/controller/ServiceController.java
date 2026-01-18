package hotel.controller;

import di.Component;
import di.Inject;
import hotel.controller.interfaces.IServiceController;
import hotel.model.Service;
import hotel.service.interfaces.IServiceManager;
import hotel.view.enums.ServiceSortOption;

import java.util.List;

@Component
public class ServiceController implements IServiceController {

    @Inject
    private IServiceManager serviceManager;

    @Override
    public Service addService(Service service) {
        return serviceManager.addService(service);
    }

    @Override
    public List<Service> getServices(ServiceSortOption option) {
        return serviceManager.getSortedServices(option);
    }
}
