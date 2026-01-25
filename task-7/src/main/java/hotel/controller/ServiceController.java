package task_11.controller;

import di.Component;
import di.Inject;
import task_11.controller.interfaces.IServiceController;
import task_11.model.Service;
import task_11.service.interfaces.IServiceManager;
import task_11.view.enums.ServiceSortOption;

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
