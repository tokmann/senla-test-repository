package task_5.service;

import task_5.model.Service;
import task_5.model.repository.ServiceRepository;
import task_5.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceManager {

    private final ServiceRepository repository;

    public ServiceManager(ServiceRepository repository) {
        this.repository = repository;
    }

    public Service addService(String name, String description, double price, LocalDate date) {
        Optional<Service> existing = repository.findByName(name);
        Service service = new Service(name, description, price, date);
        if (existing.isEmpty()) {
            repository.save(service);
            return service;
        }
        return null;
    }

    public void changeServicePrice(String serviceName, double newPrice) {
        repository.findByName(serviceName).ifPresent(service -> service.setPrice(newPrice));
    }

    public List<Service> getSortedServices(ServiceSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    public Service findByName(String name) {
        return repository.findAll().stream()
                .filter(s -> s.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

}