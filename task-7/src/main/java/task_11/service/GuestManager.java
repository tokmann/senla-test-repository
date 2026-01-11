package task_11.service;

import di.Component;
import di.Inject;
import task_11.db.TransactionManager;
import task_11.db.interfaces.GuestServiceRepository;
import task_11.db.interfaces.RoomRepository;
import task_11.exceptions.ValidationException;
import task_11.exceptions.guests.GuestAlreadyCheckedInException;
import task_11.exceptions.guests.GuestException;
import task_11.exceptions.guests.GuestNotCheckedInException;
import task_11.exceptions.guests.GuestNotFoundException;
import task_11.exceptions.services.ServiceException;
import task_11.exceptions.services.ServiceNotFoundException;
import task_11.model.Guest;
import task_11.model.Room;
import task_11.model.Service;
import task_11.db.interfaces.GuestRepository;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.service.interfaces.IServiceManager;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.ServiceSortOption;

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
        System.out.println("[GuestManager.addGuest] Начало добавления гостя: " + guest.getFullName());
        transactionManager.beginTransaction();
        try {
            guestRepository.save(guest);
            transactionManager.commitTransaction();
            System.out.println("[GuestManager.addGuest] Гость успешно добавлен: " + guest.getFullName() + " (ID: " + guest.getId() + ")");
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            System.out.println("[GuestManager.addGuest] ОШИБКА при добавлении гостя: " + e.getMessage());
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
        System.out.println("[GuestManager.removeGuest] Начало удаления гостя ID: " + guest.getId());

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
            System.out.println("[GuestManager.removeGuest] Гость успешно удален: " + loadedGuest.getFullName());
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            System.out.println("[GuestManager.removeGuest] ОШИБКА при удалении гостя: " + e.getMessage());
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
            System.out.println("[GuestManager.getAllGuests] Получено гостей из БД: " + guests.size());

            guests.forEach(guest -> {
                guestRepository.loadRoomForGuest(guest);
                guest.setServices(guestServiceRepository.findServicesByGuestId(guest.getId()));
                System.out.println("[GuestManager.getAllGuests] Загружен гость: " + guest.getFullName() +
                        ", комната: " + (guest.getRoom() != null ? guest.getRoom().getNumber() : "null") +
                        ", услуг: " + guest.getServices().size());
            });

            System.out.println("[GuestManager.getAllGuests] Успешно завершено, всего гостей: " + guests.size());
            return guests;
        } catch (Exception e) {
            System.out.println("[GuestManager.getAllGuests] ОШИБКА при получении списка гостей: " + e.getMessage());
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
        System.out.println("[GuestManager.addServiceToGuest] Начало добавления услуги ID: " + serviceId + " гостю ID: " + guestId);
        transactionManager.beginTransaction();
        try {
            guestServiceRepository.addServiceToGuest(guestId, serviceId);
            transactionManager.commitTransaction();
            System.out.println("[GuestManager.addServiceToGuest] Услуга успешно добавлена гостю ID: " + guestId);
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            System.out.println("[GuestManager.addServiceToGuest] ОШИБКА при добавлении услуги гостю: " + e.getMessage());
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

        System.out.println("[GuestMgr.checkInGuest] Начало заселения гостя ID: " + guestId + " в комнату " + roomNumber);
        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));
            System.out.println("[GuestMgr.checkInGuest] Найден гость: " + guest.getFullName() + " (ID: " + guest.getId() + ")");

            guestRepository.loadRoomForGuest(guest);

            if (guest.getRoom() != null) {
                System.out.println("[GuestMgr.checkInGuest] Гость уже заселен в комнату: " + guest.getRoom().getNumber());
                throw new GuestAlreadyCheckedInException(guestId);
            }

            System.out.println("[GuestMgr.checkInGuest] Вызов roomManager.checkIn для комнаты " + roomNumber);
            boolean result = roomManager.checkIn(roomNumber, List.of(guest), checkIn, checkOut);

            if (result) {
                transactionManager.commitTransaction();
                System.out.println("[GuestMgr.checkInGuest] Заселение успешно завершено");
                return true;
            } else {
                transactionManager.rollbackTransaction();
                System.out.println("[GuestMgr.checkInGuest] Заселение не удалось, транзакция откачена");
                return false;
            }
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            System.out.println("[GuestMgr.checkInGuest] ОШИБКА при заселении гостя: " + e.getMessage());
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
        System.out.println("[GuestManager.checkOutGuest] Начало выселения гостя ID: " + guestId);
        transactionManager.beginTransaction();
        try {
            Guest guest = guestRepository.findById(guestId)
                    .orElseThrow(() -> new GuestNotFoundException(guestId));
            System.out.println("[GuestManager.checkOutGuest] Найден гость: " + guest.getFullName() + " (ID: " + guest.getId() + ")");

            guestRepository.loadRoomForGuest(guest);

            Room room = guest.getRoom();
            if (room == null) {
                System.out.println("[GuestManager.checkOutGuest] Гость не заселен ни в одну комнату");
                throw new GuestNotCheckedInException(guestId);
            }
            System.out.println("[GuestManager.checkOutGuest] Гость заселен в комнату: " + room.getNumber());

            List<Guest> remainingGuests = guestRepository.findByRoomId(room.getId()).stream()
                    .filter(g -> g.getId() != guestId)
                    .collect(Collectors.toList());

            if (remainingGuests.isEmpty()) {
                System.out.println("[GuestManager.checkOutGuest] Последний гость в комнате, выселение всей комнаты");
                roomManager.checkOut(room.getNumber());
            } else {
                System.out.println("[GuestManager.checkOutGuest] Есть другие гости в комнате, обновление только этого гостя");
                guest.setRoom(null);
                guest.setRoomId(null);
                guestRepository.save(guest);

                room.setGuests(remainingGuests);
                roomRepository.save(room);
            }

            transactionManager.commitTransaction();
            System.out.println("[GuestManager.checkOutGuest] Выселение успешно завершено");
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            System.out.println("[GuestManager.checkOutGuest] ОШИБКА при выселении гостя: " + e.getMessage());
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
        System.out.println("[GuestManager.addServiceToGuestByName] Начало добавления услуги '" + serviceName + "' гостю '" + guestFullName + "'");

        try {
            Guest guest = findGuestByFullName(guestFullName);
            if (guest == null) {
                System.out.println("[GuestManager.addServiceToGuestByName] Гость не найден: " + guestFullName);
                throw new GuestNotFoundException(guestFullName);
            }
            System.out.println("[GuestManager.addServiceToGuestByName] Найден гость: " + guest.getFullName() + " (ID: " + guest.getId() + ")");

            Service service = serviceManager.findByName(serviceName);
            if (service == null) {
                System.out.println("[GuestManager.addServiceToGuestByName] Услуга не найдена: " + serviceName);
                throw new ServiceNotFoundException(serviceName);
            }
            System.out.println("[GuestManager.addServiceToGuestByName] Найдена услуга: " + service.getName() + " (ID: " + service.getId() + ")");

            addServiceToGuest(guest.getId(), service.getId());
            System.out.println("[GuestManager.addServiceToGuestByName] Услуга успешно добавлена гостю");
        } catch (Exception e) {
            System.out.println("[GuestManager.addServiceToGuestByName] ОШИБКА при добавлении услуги гостю: " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("Ошибка при добавлении услуги гостю", e);
        }
    }
}