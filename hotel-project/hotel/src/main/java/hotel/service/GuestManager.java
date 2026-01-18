package hotel.service;

import di.Component;
import di.Inject;
import hotel.controller.GuestController;
import hotel.db.TransactionManager;
import hotel.db.interfaces.GuestServiceRepository;
import hotel.db.interfaces.RoomRepository;
import hotel.exceptions.ValidationException;
import hotel.exceptions.guests.GuestAlreadyCheckedInException;
import hotel.exceptions.guests.GuestException;
import hotel.exceptions.guests.GuestNotCheckedInException;
import hotel.exceptions.guests.GuestNotFoundException;
import hotel.exceptions.services.ServiceException;
import hotel.exceptions.services.ServiceNotFoundException;
import hotel.model.Guest;
import hotel.model.Room;
import hotel.model.Service;
import hotel.db.interfaces.GuestRepository;
import hotel.service.interfaces.IGuestManager;
import hotel.service.interfaces.IRoomManager;
import hotel.service.interfaces.IServiceManager;
import hotel.view.enums.GuestSortOption;
import hotel.view.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Менеджер для управления гостями отеля.
 * Содержит бизнес-логику для регистрации, заселения, выселения гостей
 * и управления их услугами.
 */
@Component
public class GuestManager implements IGuestManager {

    private static final Logger log = LoggerFactory.getLogger(GuestManager.class);

    @Inject
    private GuestRepository guestRepository;

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private GuestServiceRepository guestServiceRepository;

    @Inject
    private IRoomManager roomManager;

    @Inject
    private IServiceManager serviceManager;

    @Inject
    private TransactionManager transactionManager;

    /**
     * Добавляет нового гостя в систему.
     * @param guest гость для добавления
     */
    @Override
    public void addGuest(Guest guest) {
        log.info("Начало обработки команды: addGuest, guest={}", guest);

        transactionManager.beginTransaction();
        try {
            guestRepository.save(guest);
            transactionManager.commitTransaction();

            log.info("Успешно выполнена команда: addGuest, guest={}", guest);

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: addGuest, guest={}", guest, e);
            throw new GuestException("Ошибка при добавлении гостя", e);
        }
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
            log.error("Ошибка выполнения команды: removeGuest - guest=null");
            throw new ValidationException("Гость не может быть null");
        }

        transactionManager.beginTransaction();
        try {
            Guest loadedGuest = guestRepository.findById(guest.getId())
                    .orElseThrow(() -> new GuestNotFoundException(guest.getId()));

            if (loadedGuest.getRoom() != null) {
                throw new GuestException("Невозможно удалить заселенного гостя " + loadedGuest.getFullName() +
                        " из комнаты " + loadedGuest.getRoom().getNumber());
            }

            guestRepository.delete(loadedGuest);
            transactionManager.commitTransaction();

            log.info("Успешно выполнена команда: removeGuest, guest={}", guest);

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: removeGuest, guest={}", guest, e);
            throw new GuestException("Ошибка при удалении гостя", e);
        }
    }

    /**
     * Возвращает список всех гостей с загруженными связанными данными.
     * @return список всех гостей
     */
    @Override
    public List<Guest> getAllGuests() {
        log.info("Начало обработки команды: getAllGuests");

        try {
            List<Guest> guests = guestRepository.findAll();

            guests.forEach(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
            });

            log.info("Успешно выполнена команда: getAllGuests, найдено={} гостей", guests.size());
            return guests;

        } catch (Exception e) {
            log.error("Ошибка выполнения команды: getAllGuests", e);
            throw new GuestException("Ошибка при получении списка гостей", e);
        }
    }

    /**
     * Возвращает список незаселенных гостей.
     * @return список гостей без комнаты
     */
    @Override
    public List<Guest> getGuestsNotCheckedIn() {
        log.info("Начало обработки команды: getGuestsNotCheckedIn");
        List<Guest> guestsNotCheckedIn = getAllGuests().stream()
                .filter(guest -> guest.getRoom() == null)
                .collect(Collectors.toList());
        log.info("Успешно выполнена команда: getGuestsNotCheckedIn, найдено={} гостей", guestsNotCheckedIn.size());
        return guestsNotCheckedIn;
    }

    /**
     * Возвращает список заселенных гостей.
     * @return список гостей с комнатой
     */
    @Override
    public List<Guest> getGuestsCheckedIn() {
        log.info("Начало обработки команды: getGuestsCheckedIn");
        List<Guest> checkedInGuests = getAllGuests().stream()
                .filter(guest -> guest.getRoom() != null)
                .collect(Collectors.toList());
        log.info("Успешно выполнена команда: getGuestsCheckedIn, найдено={} гостей", checkedInGuests.size());
        return checkedInGuests;
    }

    /**
     * Возвращает общее количество гостей в системе.
     * @return количество гостей
     */
    @Override
    public int countGuests() {
        log.info("Начало обработки команды: countGuests");

        transactionManager.beginTransaction();
        try {
            int count = guestRepository.count();
            transactionManager.commitTransaction();

            log.info("Успешно выполнена команда: countGuests, count={}", count);
            return count;

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: countGuests", e);
            throw new GuestException("Ошибка при подсчете количества гостей", e);
        }
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
            log.error("Ошибка выполнения команды: getSortedGuestServices - guest=null");
            throw new ValidationException("Гость не может быть null");
        }

        transactionManager.beginTransaction();
        try {
            List<Service> services = guestServiceRepository.findServicesByGuestId(guest.getId());
            transactionManager.commitTransaction();

            List<Service> sorted = services.stream().sorted(option.getComparator()).collect(Collectors.toList());
            log.info("Успешно выполнена команда: getSortedGuestServices, guest={}, найдено услуг={}",
                    guest, sorted.size());
            return sorted;

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: getSortedGuestServices, guest={}", guest, e);
            throw new ServiceException("Ошибка при получении отсортированных услуг гостя", e);
        }
    }

    /**
     * Возвращает список гостей, отсортированных по указанному критерию.
     * @param option критерий сортировки
     * @return отсортированный список гостей
     */
    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        log.info("Начало обработки команды: getSortedGuests");
        List<Guest> sortedGuests = getAllGuests().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
        log.info("Успешно выполнена команда: getSortedGuests, найдено={} гостей", sortedGuests.size());
        return sortedGuests;
    }

    /**
     * Возвращает гостя по его идентификатору.
     * @param id идентификатор гостя
     * @return Optional с гостем или пустой Optional, если гость не найден
     */
    @Override
    public Optional<Guest> getGuestById(long id) {
        log.info("Начало обработки команды: getGuestById, id={}", id);

        transactionManager.beginTransaction();
        try {
            Optional<Guest> guestOpt = guestRepository.findById(id);
            guestOpt.ifPresent(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
            });

            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: getGuestById, id={}, найден={}", id, guestOpt.isPresent());
            return guestOpt;

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: getGuestById, id={}", id, e);
            throw new GuestException("Ошибка при получении гостя по ID", e);
        }
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
            log.error("Ошибка выполнения команды: findGuestByFullName - fullName пустое");
            throw new ValidationException("Полное имя гостя не может быть пустым");
        }

        String normalizedFullName = fullName.trim().toLowerCase();
        Guest guest = getAllGuests().stream()
                .filter(g -> g.getFullName().toLowerCase().equals(normalizedFullName))
                .findFirst()
                .orElse(null);

        log.info("Успешно выполнена команда: findGuestByFullName, fullName={}, найден={}", fullName, guest != null);
        return guest;
    }

    /**
     * Добавляет услугу гостю по идентификаторам.
     * @param guestId идентификатор гостя
     * @param serviceId идентификатор услуги
     */
    @Override
    public void addServiceToGuest(long guestId, long serviceId) {
        log.info("Начало обработки команды: addServiceToGuest, guestId={}, serviceId={}", guestId, serviceId);

        transactionManager.beginTransaction();
        try {
            guestServiceRepository.addServiceToGuest(guestId, serviceId);
            transactionManager.commitTransaction();

            log.info("Успешно выполнена команда: addServiceToGuest, guestId={}, serviceId={}", guestId, serviceId);

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: addServiceToGuest, guestId={}, serviceId={}", guestId, serviceId, e);
            throw new ServiceException("Ошибка при добавлении услуги гостю", e);
        }
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
            log.error("Ошибка выполнения команды: checkInGuest - даты пустые");
            throw new ValidationException("Даты заселения и выселения не могут быть пустыми");
        }
        if (!checkOut.isAfter(checkIn)) {
            log.error("Ошибка выполнения команды: checkInGuest - дата выселения до заселения");
            throw new ValidationException("Дата выселения должна быть после даты заселения");
        }

        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));

            guestRepository.loadRoomForGuest(guest);

            if (guest.getRoom() != null) {
                throw new GuestAlreadyCheckedInException(guestId);
            }

            boolean result = roomManager.checkIn(roomNumber, List.of(guest), checkIn, checkOut);

            if (result) {
                transactionManager.commitTransaction();
                log.info("Успешно выполнена команда: checkInGuest, guestId={}, roomNumber={}", guestId, roomNumber);
                return true;
            } else {
                transactionManager.rollbackTransaction();
                log.error("Ошибка выполнения команды: checkInGuest, guestId={}, roomNumber={} - заселение не удалось",
                        guestId, roomNumber);
                return false;
            }
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: checkInGuest, guestId={}, roomNumber={}", guestId, roomNumber, e);
            throw new GuestException("Ошибка при заселении гостя", e);
        }
    }

    /**
     * Выселяет гостя из комнаты.
     * @param guestId идентификатор гостя
     */
    @Override
    public void checkOutGuest(long guestId) {
        log.info("Начало обработки команды: checkOutGuest, guestId={}", guestId);

        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));

            guestRepository.loadRoomForGuest(guest);

            Room room = guest.getRoom();
            if (room == null) {
                log.error("Ошибка выполнения команды: checkOutGuest - гость не заселен, guestId={}", guestId);
                throw new GuestNotCheckedInException(guestId);
            }

            List<Guest> remainingGuests = guestRepository.findByRoomId(room.getId()).stream()
                    .filter(g -> g.getId() != guestId)
                    .collect(Collectors.toList());

            if (remainingGuests.isEmpty()) {
                roomManager.checkOut(room.getNumber());
            } else {
                guest.setRoom(null);
                guest.setRoomId(null);
                guestRepository.save(guest);

                room.setGuests(remainingGuests);
                roomRepository.save(room);
            }

            transactionManager.commitTransaction();
            log.info("Успешно выполнена команда: checkOutGuest, guestId={}", guestId);

        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            log.error("Ошибка выполнения команды: checkOutGuest, guestId={}", guestId, e);
            throw new GuestException("Ошибка при выселении гостя", e);
        }
    }

    /**
     * Добавляет услугу гостю по имени гостя и названию услуги.
     * @param guestFullName полное имя гостя
     * @param serviceName название услуги
     */
    @Override
    public void addServiceToGuestByName(String guestFullName, String serviceName) {
        log.info("Начало обработки команды: addServiceToGuestByName, guestFullName={}, serviceName={}",
                guestFullName, serviceName);

        if (guestFullName == null || guestFullName.trim().isEmpty()) {
            log.error("Ошибка выполнения команды: addServiceToGuestByName - guestFullName пустое");
            throw new ValidationException("Полное имя гостя не может быть пустым");
        }
        if (serviceName == null || serviceName.trim().isEmpty()) {
            log.error("Ошибка выполнения команды: addServiceToGuestByName - serviceName пустое");
            throw new ValidationException("Название услуги не может быть пустым");
        }

        try {
            Guest guest = findGuestByFullName(guestFullName);
            if (guest == null) {
                log.error("Ошибка выполнения команды: addServiceToGuestByName - гость не найден, guestFullName={}", guestFullName);
                throw new GuestNotFoundException(guestFullName);
            }

            Service service = serviceManager.findByName(serviceName);
            if (service == null) {
                log.error("Ошибка выполнения команды: addServiceToGuestByName - услуга не найдена, serviceName={}", serviceName);
                throw new ServiceNotFoundException(serviceName);
            }

            addServiceToGuest(guest.getId(), service.getId());
            log.info("Успешно выполнена команда: addServiceToGuestByName, guestFullName={}, serviceName={}",
                    guestFullName, serviceName);

        } catch (Exception e) {
            log.error("Ошибка выполнения команды: addServiceToGuestByName, guestFullName={}, serviceName={}",
                    guestFullName, serviceName, e);
            throw new ServiceException("Ошибка при добавлении услуги гостю", e);
        }
    }
}