package task_11.service.interfaces;

import task_11.model.Service;
import task_11.view.enums.ServiceSortOption;

import java.util.List;
import java.util.Optional;


public interface IServiceManager {

    Service addService(Service service);
    void changeServicePrice(String serviceName, double newPrice);
    List<Service> getSortedServices(ServiceSortOption option);
    List<Service> getAllServices();
    Service findByName(String name);
    Optional<Service> getServiceById(long id);
}