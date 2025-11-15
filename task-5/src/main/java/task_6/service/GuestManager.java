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

    /**
     * Добавляет нового гостя в систему.
     * @param guest гость для добавления
     */
    public void addGuest(Guest guest) {
        repository.save(guest);
    }

    /**
     * Удаляет гостя из системы.
     * Гость может быть удален только если он не заселен в комнату.
     * @param guest гость для удаления
     * @return true если удаление успешно, false если гость заселен в комнату
     */
    public boolean removeGuest(Guest guest) {
        if (guest != null && guest.getGuestRoom() == null) {
            repository.delete(guest);
            return true;
        }
        return false;
    }

    /**
     * Возвращает всех гостей системы.
     * @return список всех гостей
     */
    public List<Guest> getAllGuests() {
        return repository.findAll();
    }

    /**
     * Возвращает гостей, которые не заселены в комнаты.
     * @return список незаселенных гостей
     */
    public List<Guest> getGuestsNotCheckedIn() {
        return repository.findAll().stream()
                .filter(g -> g.getGuestRoom() == null)
                .collect(Collectors.toList());
    }


    /**
     * Возвращает гостей, заселенных в комнаты.
     * @return список заселенных гостей
     */
    public List<Guest> getGuestsCheckedIn() {
        return repository.findAll().stream()
                .filter(g -> g.getGuestRoom() != null)
                .collect(Collectors.toList());
    }

    /**
     * Подсчитывает общее количество гостей в системе.
     * @return количество гостей
     */
    public int countGuests() {
        return repository.findAll().size();
    }

    /**
     * Возвращает отсортированный список услуг конкретного гостя.
     * @param guest гость, чьи услуги нужно отсортировать
     * @param option критерий сортировки услуг
     * @return отсортированный список услуг гостя
     */
    public List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option) {
        return guest.getGuestServices()
                .stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех гостей, отсортированный по указанному критерию.
     * @param option критерий сортировки гостей
     * @return отсортированный список гостей
     */
    public List<Guest> getSortedGuests(GuestSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Находит гостя по идентификатору.
     * @param id идентификатор гостя
     * @return Optional с найденным гостем или empty если не найден
     */
    public Optional<Guest> getGuestById(long id) {
        return repository.findById(id);
    }

    /**
     * Ищет гостя по полному имени (без учёта регистра).
     * @param fullName полное имя гостя в формате "Имя Фамилия"
     * @return найденный гость или null если не найден
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
     * Предотвращает дублирование услуг у одного гостя.
     * @param guest гость, которому добавляется услуга
     * @param service услуга для добавления
     */
    public void addServiceToGuest(Guest guest, Service service) {
        if (guest == null || service == null) return;

        List<Service> services = guest.getGuestServices();
        if (!services.contains(service)) {
            services.add(service);
        }
    }

    /**
     * Заселяет существующего гостя в комнату на указанный период.
     * Проверяет, что гость существует и не заселен в другую комнату.
     * @param guestId идентификатор гостя
     * @param roomNumber номер комнаты для заселения
     * @param checkIn дата заселения
     * @param checkOut дата выселения
     * @param roomManager менеджер комнат для выполнения операции заселения
     * @return true если заселение успешно, false в случае ошибки
     */
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

    /**
     * Выселяет гостя из комнаты.
     * Гость остается в системе, но теряет связь с комнатой.
     * Если комната становится пустой, она помечается как свободная.
     * @param guestId идентификатор гостя
     * @return true если выселение успешно, false если гость не найден или не заселен
     */
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

    /**
     * Добавляет услугу гостю по имени гостя и названию услуги.
     * Находит гостя по полному имени и услугу по названию.
     * @param guestFullName полное имя гостя
     * @param serviceName название услуги
     * @param serviceManager менеджер услуг для поиска услуги
     * @return true если операция успешна, false если гость или услуга не найдены
     */
    public boolean addServiceToGuestByName(String guestFullName, String serviceName, ServiceManager serviceManager) {
        Guest guest = findGuestByFullName(guestFullName);
        if (guest == null) return false;

        Service service = serviceManager.findByName(serviceName);
        if (service == null) return false;

        addServiceToGuest(guest, service);
        return true;
    }

}
