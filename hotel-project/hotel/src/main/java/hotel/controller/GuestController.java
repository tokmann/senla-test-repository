package hotel.controller;

import hotel.controller.interfaces.IGuestController;
import hotel.model.Guest;
import hotel.model.Service;
import hotel.service.interfaces.IGuestManager;
import hotel.view.enums.GuestSortOption;
import hotel.view.enums.ServiceSortOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Контроллер для обработки команд, связанных с гостями отеля.
 * Отвечает за приём пользовательских команд и делегирует
 * бизнес-логику в слой {@link IGuestManager}.
 */
@Component
public class GuestController implements IGuestController {

    private static final Logger log = LoggerFactory.getLogger(GuestController.class);

    private final IGuestManager guestManager;

    public GuestController(IGuestManager guestManager) {
        this.guestManager = guestManager;
    }

    /**
     * Регистрирует нового гостя в системе.
     * @param guest объект гостя
     * @return зарегистрированный гость
     */
    @Override
    public Guest registerGuest(Guest guest) {
        log.info("Начало обработки команды: registerGuest, guest={}", guest);
        guestManager.addGuest(guest);
        return guest;
    }

    /**
     * Заселяет гостя в указанный номер на заданный период.
     * @param guestId   идентификатор гостя
     * @param roomNumber номер комнаты
     * @param checkIn   дата заезда
     * @param checkOut  дата выезда
     * @return true, если заселение прошло успешно
     */
    @Override
    public boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn, LocalDate checkOut) {
        log.info("Начало обработки команды: checkInGuest, guestId={}, roomNumber={}, checkIn={}, checkOut={}",
                guestId, roomNumber, checkIn, checkOut);
        return guestManager.checkInGuest(guestId, roomNumber, checkIn, checkOut);
    }

    /**
     * Выписывает гостя из номера.
     * @param guestId идентификатор гостя
     */
    @Override
    public void checkOutGuest(long guestId) {
        log.info("Начало обработки команды: checkOutGuest, guestId={}", guestId);
        guestManager.checkOutGuest(guestId);
    }

    /**
     * Возвращает список гостей, отсортированных по заданному критерию.
     * @param option параметр сортировки
     * @return список гостей
     */
    @Override
    public List<Guest> getSortedGuests(GuestSortOption option) {
        log.info("Начало обработки команды: getSortedGuests, option={}", option);
        return guestManager.getSortedGuests(option);
    }

    /**
     * Находит гостя по полному имени.
     * @param fullName полное имя гостя
     * @return найденный гость
     */
    @Override
    public Guest findGuestByFullName(String fullName) {
        log.info("Начало обработки команды: findGuestByFullName, fullName={}", fullName);
        return guestManager.findGuestByFullName(fullName);
    }

    /**
     * Возвращает список услуг, оказанных гостю, с сортировкой.
     * @param guest  гость
     * @param option параметр сортировки услуг
     * @return список услуг
     */
    @Override
    public List<Service> getGuestServices(Guest guest, ServiceSortOption option) {
        log.info("Начало обработки команды: getGuestServices, guest={}, option={}", guest, option);
        return guestManager.getSortedGuestServices(guest, option);
    }

    /**
     * Возвращает общее количество зарегистрированных гостей.
     * @return количество гостей
     */
    @Override
    public int countGuests() {
        log.info("Начало обработки команды: countGuests");
        return guestManager.countGuests();
    }

    /**
     * Добавляет услугу гостю по имени гостя и названию услуги.
     * @param guestFullName полное имя гостя
     * @param serviceName   название услуги
     */
    @Override
    public void addServiceToGuestByName(String guestFullName, String serviceName) {
        log.info("Начало обработки команды: addServiceToGuestByName, fullName={}, service={}",
                guestFullName, serviceName);
        guestManager.addServiceToGuestByName(guestFullName, serviceName);
    }
}
