package hotel.service;

import di.Component;
import di.Inject;
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
        transactionManager.beginTransaction();
        try {
            guestRepository.save(guest);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            e.printStackTrace();
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
        if (guest == null) {
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
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            e.printStackTrace();
            throw new GuestException("Ошибка при удалении гостя", e);
        }
    }

    /**
     * Возвращает список всех гостей с загруженными связанными данными.
     * @return список всех гостей
     */
    @Override
    public List<Guest> getAllGuests() {
        System.out.println("[GuestManager.getAllGuests] Начало получения всех гостей");
        try {
            List<Guest> guests = guestRepository.findAll();

            guests.forEach(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
            });

            return guests;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuestException("Ошибка при получении списка гостей", e);
        }
    }

    /**
     * Возвращает список незаселенных гостей.
     * @return список гостей без комнаты
     */
    @Override
    public List<Guest> getGuestsNotCheckedIn() {
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
        transactionManager.beginTransaction();
        try {
            int count = guestRepository.count();
            transactionManager.commitTransaction();
            return count;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
        if (guest == null) {
            throw new ValidationException("Гость не может быть null");
        }

        transactionManager.beginTransaction();
        try {
            List<Service> services = guestServiceRepository.findServicesByGuestId(guest.getId());
            transactionManager.commitTransaction();
            return services.stream()
                    .sorted(option.getComparator())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
        return getAllGuests().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает гостя по его идентификатору.
     * @param id идентификатор гостя
     * @return Optional с гостем или пустой Optional, если гость не найден
     */
    @Override
    public Optional<Guest> getGuestById(long id) {
        transactionManager.beginTransaction();
        try {
            Optional<Guest> guestOpt = guestRepository.findById(id);
            guestOpt.ifPresent(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
            });
            transactionManager.commitTransaction();
            return guestOpt;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
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
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new ValidationException("Полное имя гостя не может быть пустым");
        }

        String normalizedFullName = fullName.trim().toLowerCase();
        return getAllGuests().stream()
                .filter(guest -> guest.getFullName().toLowerCase().equals(normalizedFullName))
                .findFirst()
                .orElse(null);
    }

    /**
     * Добавляет услугу гостю по идентификаторам.
     * @param guestId идентификатор гостя
     * @param serviceId идентификатор услуги
     */
    @Override
    public void addServiceToGuest(long guestId, long serviceId) {
        transactionManager.beginTransaction();
        try {
            guestServiceRepository.addServiceToGuest(guestId, serviceId);
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            e.printStackTrace();
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
            throw new ValidationException("Даты заселения и выселения не могут быть пустыми");
        }
        if (!checkOut.isAfter(checkIn)) {
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
                return true;
            } else {
                transactionManager.rollbackTransaction();
                return false;
            }
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            e.printStackTrace();
            throw new GuestException("Ошибка при заселении гостя", e);
        }
    }

    /**
     * Выселяет гостя из комнаты.
     * @param guestId идентификатор гостя
     */
    @Override
    public void checkOutGuest(long guestId) {
        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));

            guestRepository.loadRoomForGuest(guest);

            Room room = guest.getRoom();
            if (room == null) {
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
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            e.printStackTrace();
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
        if (guestFullName == null || guestFullName.trim().isEmpty()) {
            throw new ValidationException("Полное имя гостя не может быть пустым");
        }
        if (serviceName == null || serviceName.trim().isEmpty()) {
            throw new ValidationException("Название услуги не может быть пустым");
        }

        try {
            Guest guest = findGuestByFullName(guestFullName);
            if (guest == null) {
                throw new GuestNotFoundException(guestFullName);
            }

            Service service = serviceManager.findByName(serviceName);
            if (service == null) {
                throw new ServiceNotFoundException(serviceName);
            }

            addServiceToGuest(guest.getId(), service.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException("Ошибка при добавлении услуги гостю", e);
        }
    }
}