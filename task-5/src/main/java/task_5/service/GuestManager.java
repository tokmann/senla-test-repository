package task_5.service;

import task_5.model.Guest;
import task_5.model.Service;
import task_5.model.repository.GuestRepository;
import task_5.view.enums.GuestSortOption;
import task_5.view.enums.ServiceSortOption;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GuestManager {

    private final GuestRepository repository;

    public GuestManager(GuestRepository repository) {
        this.repository = repository;
    }

    public void addGuest(Guest guest) {
        repository.save(guest);
    }

    public void removeGuest(Guest guest) {
        repository.delete(guest);
    }

    public List<Guest> getAllGuests() {
        return repository.findAll();
    }

    public int countGuests() {
        return repository.findAll().size();
    }

    public List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option) {
        return guest.getGuestServices()
                .stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    public List<Guest> getSortedGuests(GuestSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    public Guest findGuestByFullName(String fullName) {
        return repository.findAll()
                .stream()
                .filter(g -> g.getFullName().equalsIgnoreCase(fullName))
                .findFirst()
                .orElse(null);
    }

    public void addServiceToGuest(Guest guest, Service service) {
        if (guest == null || service == null) return;

        List<Service> services = guest.getGuestServices();
        if (!services.contains(service)) {
            services.add(service);
        }
    }
}
