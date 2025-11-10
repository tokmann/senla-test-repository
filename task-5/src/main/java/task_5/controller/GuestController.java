package task_5.controller;

import task_5.model.Guest;
import task_5.model.Service;
import task_5.service.GuestManager;
import task_5.service.RoomManager;
import task_5.service.ServiceManager;
import task_5.view.enums.GuestSortOption;
import task_5.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    /** Регистрация нового гостя с автоматическим заселением в номер */
    public Guest registerGuest(String firstName, String lastName, int age, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        Guest guest = new Guest(age, firstName, lastName, null, new ArrayList<>());
        boolean ok = roomManager.checkIn(roomNumber, List.of(guest), checkIn, checkOut);
        if (ok) {
            guestManager.addGuest(guest);
            return guest;
        }
        return null;
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
        Guest guest = guestManager.findGuestByFullName(guestFullName);
        if (guest == null) return false;

        Service service = serviceManager.findByName(serviceName);
        if (service == null) return false;

        guestManager.addServiceToGuest(guest, service);
        return true;
    }


}
