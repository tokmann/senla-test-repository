package task_6.service;

import task_6.model.Guest;
import task_6.model.Room;
import task_6.model.Service;
import task_6.repository.interfaces.GuestRepository;
import task_6.view.enums.GuestSortOption;
import task_6.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервисный слой для управления гостями.
 * Отвечает за операции CRUD над сущностью {@link Guest},
 * а также за бизнес-логику, связанную с гостями:
 * сортировка, поиск, добавление услуг и т.д.
 */
public class GuestManager {

    private final GuestRepository repository;

    public GuestManager(GuestRepository repository) {
        this.repository = repository;
    }

    public void addGuest(Guest guest) {
        repository.save(guest);
    }

    public boolean removeGuest(Guest guest) {
        if (guest != null && guest.getGuestRoom() == null) {
            repository.delete(guest);
            return true;
        }
        return false;
    }

    public List<Guest> getAllGuests() {
        return repository.findAll();
    }

    public List<Guest> getGuestsNotCheckedIn() {
        return repository.findAll().stream()
                .filter(g -> g.getGuestRoom() == null)
                .collect(Collectors.toList());
    }

    public List<Guest> getGuestsCheckedIn() {
        return repository.findAll().stream()
                .filter(g -> g.getGuestRoom() != null)
                .collect(Collectors.toList());
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

    public Optional<Guest> getGuestById(long id) {
        return repository.findAll().stream()
                .filter(g -> g.getId() == id)
                .findFirst();
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

    /** Метод для заселения существующего гостя */
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut, RoomManager roomManager) {
        Optional<Guest> optionalGuest = getGuestById(guestId);
        if (optionalGuest.isEmpty()) {
            return false;
        }
        Guest guest = optionalGuest.get();
        if (guest.getGuestRoom() != null) {
            return false;
        }
        return roomManager.checkIn(roomNumber, List.of(guest), checkIn, checkOut);
    }

    /** Выселение гостя из комнаты (но он остается в системе) */
    public boolean checkOutGuest(long guestId) {
        Optional<Guest> optionalGuest = getGuestById(guestId);
        if (optionalGuest.isEmpty()) {
            return false;
        }
        Guest guest = optionalGuest.get();
        Room room = guest.getGuestRoom();
        if (room == null) {
            return false;
        }

        room.getGuests().remove(guest);
        guest.setGuestRoom(null);
        if (room.getGuests().isEmpty()) {
            room.checkOut();
        }
        return true;
    }

    /** Добавление услуги гостю по имени и названию услуги */
    public boolean addServiceToGuestByName(String guestFullName, String serviceName, ServiceManager serviceManager) {
        Guest guest = findGuestByFullName(guestFullName);
        if (guest == null) return false;

        Service service = serviceManager.findByName(serviceName);
        if (service == null) return false;

        addServiceToGuest(guest, service);
        return true;
    }

}
