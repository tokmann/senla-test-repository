package task_11.controller.interfaces;

import task_11.model.Guest;
import task_11.model.Service;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс контроллера для управления гостями.
 * Отвечает за регистрацию, поиск, сортировку и добавление услуг гостям.
 */
public interface IGuestController {

    /** Регистрация нового гостя */
    Guest registerGuest(Guest guest);

    /** Метод для заселения существующего гостя */
    boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut);

    /** Выселение гостя из комнаты (но он остается в системе) */
    void checkOutGuest(long guestId);

    /** Возвращает гостей, отсортированных по заданному критерию */
    List<Guest> getSortedGuests(GuestSortOption option);

    /** Поиск гостя по полному имени */
    Guest findGuestByFullName(String fullName);

    /** Возвращает отсортированные услуги конкретного гостя */
    List<Service> getGuestServices(Guest guest, ServiceSortOption option);

    /** Подсчёт всех гостей в системе */
    int countGuests();

    /** Добавление услуги гостю по имени и названию услуги */
    void addServiceToGuestByName(String guestFullName, String serviceName);

}
