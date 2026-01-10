package task_11.service;

import di.Component;
import di.Inject;
import task_11.db.TransactionManager;
import task_11.exceptions.ValidationException;
import task_11.exceptions.services.ServiceAlreadyExistsException;
import task_11.exceptions.services.ServiceNotFoundException;
import task_11.model.Service;

import task_11.db.interfaces.ServiceRepository;
import task_11.service.interfaces.IServiceManager;
import task_11.view.enums.ServiceSortOption;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ServiceManager implements IServiceManager {

    @Inject
    private ServiceRepository serviceRepository;

    @Inject
    private TransactionManager transactionManager;

    @Override
    public Service addService(Service service) {
        validateService(service);

        transactionManager.beginTransaction();
        try {
            Optional<Service> existing = serviceRepository.findByName(service.getName());
            if (existing.isPresent()) {
                transactionManager.rollbackTransaction();
                throw new ServiceAlreadyExistsException(service.getName());
            }

            Service savedService = serviceRepository.save(service);
            transactionManager.commitTransaction();
            return savedService;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public void changeServicePrice(String serviceName, double newPrice) {
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Service name cannot be empty");
        }
        if (newPrice < 0) {
            throw new ValidationException("Service price cannot be negative");
        }

        transactionManager.beginTransaction();
        try {
            Service service = serviceRepository.findByName(serviceName)
                    .orElseThrow(() -> new ServiceNotFoundException(serviceName));

            service.setPrice(newPrice);
            serviceRepository.save(service);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Service> getSortedServices(ServiceSortOption option) {
        return getAllServices().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    @Override
    public List<Service> getAllServices() {
        transactionManager.beginTransaction();
        try {
            List<Service> services = serviceRepository.findAll();
            transactionManager.commitTransaction();
            return services;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public Service findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Service name cannot be empty");
        }

        transactionManager.beginTransaction();
        try {
            Service service = serviceRepository.findByName(name.trim())
                    .orElse(null);
            transactionManager.commitTransaction();
            return service;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public Optional<Service> getServiceById(long id) {
        transactionManager.beginTransaction();
        try {
            Optional<Service> service = serviceRepository.findById(id);
            transactionManager.commitTransaction();
            return service;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    private void validateService(Service service) {
        if (service.getName() == null || service.getName().trim().isEmpty()) {
            throw new ValidationException("Service name cannot be empty");
        }
        if (service.getPrice() < 0) {
            throw new ValidationException("Service price cannot be negative");
        }
    }
}