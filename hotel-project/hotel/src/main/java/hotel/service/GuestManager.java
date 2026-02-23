package hotel.service;

import hotel.db.interfaces.GuestServiceRepository;
import hotel.db.interfaces.RoomRepository;
import hotel.exceptions.ValidationException;
import hotel.exceptions.guests.GuestAlreadyCheckedInException;
import hotel.exceptions.guests.GuestException;
import hotel.exceptions.guests.GuestNotCheckedInException;
import hotel.exceptions.guests.GuestNotFoundException;
import hotel.exceptions.services.ServiceNotFoundException;
import hotel.model.Guest;
import hotel.model.Room;
import hotel.model.Service;
import hotel.db.interfaces.GuestRepository;
import hotel.service.interfaces.IGuestManager;
import hotel.service.interfaces.IRoomManager;
import hotel.service.interfaces.IServiceManager;
import hotel.enums.GuestSortOption;
import hotel.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер для управления гостями отеля.
 * Содержит бизнес-логику для регистрации, заселения, выселения гостей
 * и управления их услугами.
 */
@Transactional
@org.springframework.stereotype.Service
public class GuestManager implements IGuestManager {

    private static final Logger log = LoggerFactory.getLogger(GuestManager.class);

    private final GuestRepository guestRepository;
    private final RoomRepository roomRepository;
    private final GuestServiceRepository guestServiceRepository;
    private final IRoomManager roomManager;
    private final IServiceManager serviceManager;

    public GuestManager(GuestRepository guestRepository,
                        RoomRepository roomRepository,
                        GuestServiceRepository guestServiceRepository,
                        IRoomManager roomManager,
                        IServiceManager serviceManager) {
        this.guestRepository = guestRepository;
        this.roomRepository = roomRepository;
        this.guestServiceRepository = guestServiceRepository;
        this.roomManager = roomManager;
        this.serviceManager = serviceManager;
    }

    /**
     * Добавляет нового гостя в систему.
     * @param guest гость для добавления
     */
    @Override
    public Guest addGuest(Guest guest) {
        log.info("Начало обработки команды: addGuest, guest={}", guest);
        validateGuest(guest);
        guestRepository.save(guest);
        log.info("Успешно выполнена команда: addGuest, guest={}", guest);
        return guest;
    }

    /**
     * Удаляет гостя из системы.
     * Нельзя удалить заселенного гостя.
     * @param guest гость для удаления
     */
    @Override
    public void removeGuest(Guest guest) {
        log.info("Начало обработки команды: removeGuest, guest={}", guest);
        if (guest == null) {
            throw new ValidationException("Гость не может быть null");
        }
        Guest loadedGuest = guestRepository.findById(guest.getId());
        if (loadedGuest == null) {
            throw new GuestNotFoundException(guest.getId());
        }
        if (loadedGuest.getRoom() != null) {
            throw new GuestException("Невозможно удалить заселенного гостя " + loadedGuest.getFullName() +
                    " из комнаты " + loadedGuest.getRoom().getNumber());
        }
        guestRepository.delete(loadedGuest);
        log.info("Успешно выполнена команда: removeGuest, guest={}", guest);
    }

    /**
     * Возвращает список всех гостей с загруженными связанными данными.
     * @return список всех гостей
     */
    @Override
    public List<Guest> getAllGuests() {
        log.info("Начало обработки команды: getAllGuests");
        return guestRepository.findAll();
    }

    /**
     * Возвращает список незаселенных гостей.
     * @return список гостей без комнаты
     */
    @Override
    public List<Guest> getGuestsNotCheckedIn() {
        log.info("Начало обработки команды: getGuestsNotCheckedIn");
        return getAllGuests().stream()
                .filter(guest -> guest.getRoom() == null)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список заселенных гостей.
     * @return список гостей с комнатой
     */
    @Override
    public List<Guest> getGuestsCheckedIn() {
        log.info("Начало обработки команды: getGuestsCheckedIn");
        return getAllGuests().stream()
                .filter(guest -> guest.getRoom() != null)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает общее количество гостей в системе.
     * @return количество гостей
     */
    @Override
    public int countGuests() {
        log.info("Начало обработки команды: countGuests");
        return guestRepository.count();
    }

    /**
     * Возвращает услуги гостя, отсортированные по указанному критерию.
     * @param guest гость
     * @param option критерий сортировки
     * @return отсортированный список услуг
     */
    @Override
    public List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option) {
        log.info("Начало обработки команды: getSortedGuestServices, guest={}, option={}", guest, option);
        if (guest == null) {
            throw new ValidationException("Гость не может быть null");
        }
        List<Service> services = guestServiceRepository.findServicesByGuestId(guest.getId());
        return services.stream().sorted(option.getComparator()).collect(Collectors.toList());
    }

    /**
     * Возвращает список гостей, отсортированных по указанному критерию.
     * @param option критерий сортировки
     * @return отсортированный список гостей
     */
    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        log.info("Начало обработки команды: getSortedGuests");
        return getAllGuests().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает гостя по его идентификатору.
     * @param id идентификатор гостя
     * @return Guest
     */
    @Override
    public Guest getGuestById(long id) {
        log.info("Начало обработки команды: getGuestById, id={}", id);
        return guestRepository.findById(id);
    }

    /**
     * Находит гостя по полному имени.
     * @param fullName полное имя гостя
     * @return гость или null, если не найден
     */
    @Override
    public Guest findGuestByFullName(String fullName) {
        log.info("Начало обработки команды: findGuestByFullName, fullName={}", fullName);
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Полное имя гостя не может быть пустым");
        }

        String normalizedFullName = fullName.trim().toLowerCase();
        return getAllGuests().stream()
                .filter(g -> g.getFullName().toLowerCase().equals(normalizedFullName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Добавляет услугу гостю по идентификаторам.
     * @param guestId идентификатор гостя
     * @param serviceId идентификатор услуги
     */
    @Override
    public boolean addServiceToGuest(long guestId, long serviceId) {
        log.info("Начало обработки команды: addServiceToGuest, guestId={}, serviceId={}", guestId, serviceId);
        guestServiceRepository.addServiceToGuest(guestId, serviceId);
        log.info("Успешно выполнена команда: addServiceToGuest, guestId={}, serviceId={}", guestId, serviceId);
        return true;
    }

    /**
     * Заселяет гостя в указанную комнату на заданный период.
     * @param guestId идентификатор гостя
     * @param roomNumber номер комнаты
     * @param checkIn дата заселения
     * @param checkOut дата выселения
     * @return true, если заселение успешно, false в противном случае
     */
    @Override
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new ValidationException("Даты заселения и выселения не могут быть пустыми");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Дата выселения должна быть после даты заселения");
        }
        Guest guest = guestRepository.findById(guestId);
        if (guest == null) {
            throw new GuestNotFoundException(guestId);
        }
        if (guest.getRoom() != null) {
            throw new GuestAlreadyCheckedInException(guestId);
        }

        boolean result = roomManager.checkIn(roomNumber, List.of(guest), checkIn, checkOut);

        guest.setRoom(roomRepository.findByNumber(roomNumber));
        guestRepository.save(guest);

        if (!result) throw new GuestException("Заселение не удалось");

        log.info("Успешно выполнена команда: checkInGuest, guestId={}, roomNumber={}", guestId, roomNumber);
        return true;
    }

    /**
     * Выселяет гостя из комнаты.
     * @param guestId идентификатор гостя
     */
    @Override
    public boolean checkOutGuest(long guestId) {
        log.info("Начало обработки команды: checkOutGuest, guestId={}", guestId);
        Guest guest = guestRepository.findById(guestId);
        if (guest == null) {
            throw new GuestNotFoundException(guestId);
        }
        Room room = guest.getRoom();
        if (room == null) {
            throw new GuestNotCheckedInException(guestId);
        }

        boolean result = roomManager.checkOut(room.getNumber(), guestId);

        if (!result) throw new GuestException("Не удалось выселить гостя");

        log.info("Успешно выполнена команда: checkOutGuest, guestId={}", guestId);
        return true;
    }

    /**
     * Добавляет услугу гостю по имени гостя и названию услуги.
     * @param guestFullName полное имя гостя
     * @param serviceName название услуги
     */
    @Override
    public boolean addServiceToGuestByName(String guestFullName, String serviceName) {
        log.info("Начало обработки команды: addServiceToGuestByName, guestFullName={}, serviceName={}",
                guestFullName, serviceName);
        if (guestFullName == null || guestFullName.trim().isEmpty()) {
            throw new ValidationException("Полное имя гостя не может быть пустым");
        }
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }
        Guest guest = findGuestByFullName(guestFullName);
        if (guest == null) throw new GuestNotFoundException("Гость не найден: " + guestFullName);


        Service service = serviceManager.findByName(serviceName);
        if (service == null) throw new ServiceNotFoundException("Услуга не найдена: " + serviceName);

        return addServiceToGuest(guest.getId(), service.getId());
    }

    /**
     * Метод для валидации данных гостя.
     * @param guest новый гость
     */
    private void validateGuest(Guest guest) {
        if (guest == null) {
            throw new ValidationException("Гость не может быть null");
        }
        if (guest.getAge() < 0) {
            throw new ValidationException("Возраст гостя не может быть отрицательным");
        }
        if (guest.getFirstName() == null || guest.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Имя гостя не может быть пустым");
        }
        if (guest.getSecondName() == null || guest.getSecondName().trim().isEmpty()) {
            throw new ValidationException("Фамилия гостя не может быть пустой");
        }
    }
}