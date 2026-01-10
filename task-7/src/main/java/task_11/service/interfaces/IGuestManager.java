package task_11.service.interfaces;

import task_11.model.Guest;
import task_11.model.Service;
import task_11.view.enums.GuestSortOption;
import task_11.view.enums.ServiceSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс для сервиса по управлению гостями.
 * Отвечает за бизнес-логику, связанную с гостями:
 * сортировка, поиск, добавление услуг и т.д.
 */
public interface IGuestManager {

    /**
     * Добавляет нового гостя в систему.
     * @param guest гость для добавления
     */
    void addGuest(Guest guest);

    /**
     * Удаляет гостя из системы.
     * Гость может быть удален только если он не заселен в комнату.
     * @param guest гость для удаления
     */
    void removeGuest(Guest guest);

    /**
     * Возвращает всех гостей системы.
     * @return список всех гостей
     */
    List<Guest> getAllGuests();

    /**
     * Возвращает гостей, которые не заселены в комнаты.
     * @return список незаселенных гостей
     */
    List<Guest> getGuestsNotCheckedIn();


    /**
     * Возвращает гостей, заселенных в комнаты.
     * @return список заселенных гостей
     */
    List<Guest> getGuestsCheckedIn();

    /**
     * Подсчитывает общее количество гостей в системе.
     * @return количество гостей
     */
    int countGuests();

    /**
     * Возвращает отсортированный список услуг конкретного гостя.
     * @param guest гость, чьи услуги нужно отсортировать
     * @param option критерий сортировки услуг
     * @return отсортированный список услуг гостя
     */
    List<Service> getSortedGuestServices(Guest guest, ServiceSortOption option);

    /**
     * Возвращает список всех гостей, отсортированный по указанному критерию.
     * @param option критерий сортировки гостей
     * @return отсортированный список гостей
     */
    List<Guest> getSortedGuests(GuestSortOption option);

    /**
     * Находит гостя по идентификатору.
     * @param id идентификатор гостя
     * @return Optional с найденным гостем или empty если не найден
     */
    Optional<Guest> getGuestById(long id);

    /**
     * Ищет гостя по полному имени (без учёта регистра).
     * @param fullName полное имя гостя в формате "Имя Фамилия"
     * @return найденный гость или null если не найден
     */
    Guest findGuestByFullName(String fullName);

    /**
     * Добавляет услугу конкретному гостю, если она ещё не добавлена.
     * Предотвращает дублирование услуг у одного гостя.
     * @param guest гость, которому добавляется услуга
     * @param service услуга для добавления
     */
    void addServiceToGuest(Guest guest, Service service);

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
    boolean checkInGuest(long guestId, int roomNumber, LocalDate checkIn,
                                LocalDate checkOut, IRoomManager roomManager);

    /**
     * Выселяет гостя из комнаты.
     * Гость остается в системе, но теряет связь с комнатой.
     * Если комната становится пустой, она помечается как свободная.
     * @param guestId идентификатор гостя
     */
    void checkOutGuest(long guestId);

    /**
     * Добавляет услугу гостю по имени гостя и названию услуги.
     * Находит гостя по полному имени и услугу по названию.
     * @param guestFullName полное имя гостя
     * @param serviceName название услуги
     * @param serviceManager менеджер услуг для поиска услуги
     */
    void addServiceToGuestByName(String guestFullName, String serviceName,
                                        IServiceManager serviceManager);

    /**
     * Метод для синхронизации Id после десериализации
     * */
    void syncIdGen();
}
