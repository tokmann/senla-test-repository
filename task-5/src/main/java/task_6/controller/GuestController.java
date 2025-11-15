package task_6.controller;

import task_6.model.Guest;
import task_6.model.Room;
import task_6.model.Service;
import task_6.service.GuestManager;
import task_6.service.RoomManager;
import task_6.service.ServiceManager;
import task_6.view.enums.GuestSortOption;
import task_6.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления гостями.
 * Отвечает за регистрацию, поиск, сортировку и добавление услуг гостям.
 */
public class GuestController {

    private final GuestManager guestManager;
    private final RoomManager roomManager;
    private final ServiceManager serviceManager;

    public GuestController(GuestManager guestManager, RoomManager roomManager, ServiceManager serviceManager) {
        this.guestManager = guestManager;
        this.roomManager = roomManager;
        this.serviceManager = serviceManager;
    }

    /** Регистрация нового гостя */
    public Guest registerGuest(Guest guest) {
        guestManager.addGuest(guest);
        return guest;
    }

    /** Метод для заселения существующего гостя */
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return guestManager.checkInGuest(guestId, roomNumber, checkIn, checkOut, roomManager);
    }

    /** Выселение гостя из комнаты (но он остается в системе) */
    public boolean checkOutGuest(long guestId) {
        return guestManager.checkOutGuest(guestId);
    }

    /** Возвращает гостей, отсортированных по заданному критерию */
    public List<Guest> getSortedGuests(GuestSortOption option) {
        return guestManager.getSortedGuests(option);
    }

    /** Поиск гостя по полному имени */
    public Guest findGuestByFullName(String fullName) {
        return guestManager.findGuestByFullName(fullName);
    }

    /** Возвращает отсортированные услуги конкретного гостя */
    public List<Service> getGuestServices(Guest guest, ServiceSortOption option) {
        return guestManager.getSortedGuestServices(guest, option);
    }

    /** Подсчёт всех гостей в системе */
    public int countGuests() {
        return guestManager.countGuests();
    }

    /** Добавление услуги гостю по имени и названию услуги */
    public boolean addServiceToGuestByName(String guestFullName, String serviceName) {
        return guestManager.addServiceToGuestByName(guestFullName, serviceName, serviceManager);
    }

}
