package hotel.controller.interfaces;

import hotel.model.Service;
import hotel.view.enums.ServiceSortOption;

import java.util.List;


public interface IServiceController {

    Service addService(Service service);
    List<Service> getServices(ServiceSortOption option);
}
