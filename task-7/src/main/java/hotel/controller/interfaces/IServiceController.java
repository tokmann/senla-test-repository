package task_11.controller.interfaces;

import task_11.model.Service;
import task_11.view.enums.ServiceSortOption;

import java.util.List;


public interface IServiceController {

    Service addService(Service service);
    List<Service> getServices(ServiceSortOption option);
}
