package task_4.management;

import task_4.model.Service;

import java.time.LocalDate;
import java.util.*;

public class ServiceManager {

    private final List<Service> services;

    private static final Map<String, Comparator<Service>> SORTING_RULES = Map.of(
            "Цена", Comparator.comparingDouble(Service::getPrice),
            "Раздел", Comparator.comparing(Service::getName)
    );

    public ServiceManager() {
        this.services = new ArrayList<>();
    }

    public void addService(String name, String description, double price, LocalDate date) {
        if (getServiceByName(name).isPresent()) {
            System.out.println("Услуга с таким названием уже существует.");
            return;
        }
        services.add(new Service(name, description, price, date));
        System.out.println("Услуга '" + name + "' добавлена успешно.");
    }

    public void changeServicePrice(String serviceName, double newPrice) {
        Optional<Service> serviceOpt = getServiceByName(serviceName);
        if (serviceOpt.isPresent()) {
            Service service = serviceOpt.get();
            service.setPrice(newPrice);
            System.out.println("Цена услуги '" + serviceName + "' изменена на: " + newPrice);
        } else {
            System.out.println("Услуга '" + serviceName + "' не найдена.");
        }
    }

    public void displayAllServices() {
        System.out.println("ВСЕ УСЛУГИ: ");
        if (services.isEmpty()) {
            System.out.println("Услуги отсутствуют");
        } else {
            services.forEach(System.out::println);
        }
    }

    private Optional<Service> getServiceByName(String name) {
        return services.stream()
                .filter(service -> service.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Service> getAllSortedServices(String by) {
        Comparator<Service> comparator = SORTING_RULES.get(by);
        return services
                .stream()
                .sorted(comparator)
                .toList();
    }

}