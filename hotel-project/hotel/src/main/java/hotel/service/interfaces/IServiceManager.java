package hotel.service.interfaces;

import hotel.model.Service;
import hotel.enums.ServiceSortOption;

import java.util.List;


public interface IServiceManager {

    Service addService(Service service);
    void changeServicePrice(String serviceName, double newPrice);
    List<Service> getSortedServices(ServiceSortOption option);
    List<Service> getAllServices();
    Service findByName(String name);
    Service getServiceById(long id);
}