package task_11.controller;

import di.Component;
import di.Inject;
import task_11.controller.interfaces.IGuestController;
import task_11.model.Guest;
import task_11.model.Service;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.service.interfaces.IServiceManager;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для управления гостями.
 * Отвечает за регистрацию, поиск, сортировку и добавление услуг гостям.
 */
@Component
public class GuestController implements IGuestController {

    @Inject
    private IGuestManager guestManager;

    @Inject
    private IRoomManager roomManager;

    @Inject
    private IServiceManager serviceManager;

    /** Регистрация нового гостя */
    @Override
    public Guest registerGuest(Guest guest) {
        guestManager.addGuest(guest);
        return guest;
    }

    /** Метод для заселения существующего гостя */
    @Override
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        return guestManager.checkInGuest(guestId, roomNumber, checkIn, checkOut, roomManager);
    }

    /** Выселение гостя из комнаты (но он остается в системе) */
    @Override
    public void checkOutGuest(long guestId) {
        guestManager.checkOutGuest(guestId);
    }

    /** Возвращает гостей, отсортированных по заданному критерию */
    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        return guestManager.getSortedGuests(option);
    }

    /** Поиск гостя по полному имени */
    @Override
    public Guest findGuestByFullName(String fullName) {
        return guestManager.findGuestByFullName(fullName);
    }

    /** Возвращает отсортированные услуги конкретного гостя */
    @Override
    public List<Service> getGuestServices(Guest guest, ServiceSortOption option) {
        return guestManager.getSortedGuestServices(guest, option);
    }

    /** Подсчёт всех гостей в системе */
    @Override
    public int countGuests() {
        return guestManager.countGuests();
    }

    /** Добавление услуги гостю по имени и названию услуги */
    @Override
    public void addServiceToGuestByName(String guestFullName, String serviceName) {
        guestManager.addServiceToGuestByName(guestFullName, serviceName, serviceManager);
    }

}
