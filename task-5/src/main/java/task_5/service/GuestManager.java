package task_5.service;

import task_5.model.Guest;
import task_5.model.Service;
import task_5.repository.impl.InMemoryGuestRepository;
import task_5.view.enums.GuestSortOption;
import task_5.view.enums.ServiceSortOption;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный слой для управления гостями.
 * Отвечает за операции CRUD над сущностью {@link Guest},
 * а также за бизнес-логику, связанную с гостями:
 * сортировка, поиск, добавление услуг и т.д.
 */
public class GuestManager {

    private final InMemoryGuestRepository repository;

    public GuestManager(InMemoryGuestRepository repository) {
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

    /**
     * Возвращает отсортированный список услуг конкретного гостя.
     */
    public List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option) {
        return guest.getGuestServices()
                .stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех гостей, отсортированный по указанному критерию.
     */
    public List<Guest> getSortedGuests(GuestSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Ищет гостя по полному имени (без учёта регистра).
     */
    public Guest findGuestByFullName(String fullName) {
        return repository.findAll()
                .stream()
                .filter(g -> g.getFullName().equalsIgnoreCase(fullName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Добавляет услугу конкретному гостю, если она ещё не добавлена.
     */
    public void addServiceToGuest(Guest guest, Service service) {
        if (guest == null || service == null) return;

        List<Service> services = guest.getGuestServices();
        if (!services.contains(service)) {
            services.add(service);
        }
    }
}
