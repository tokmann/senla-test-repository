package task_8.service.interfaces;

import task_8.model.Guest;
import task_8.model.Room;
import task_8.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс сервиса для управления комнатами.
 * Отвечает за бронирование, выселение, установку цен,
 * техническое обслуживание, историю и фильтрацию номеров.
 */
public interface IRoomManager {

    /**
     * Добавляет номер в систему.
     * Если номер с таким ID уже существует - обновляет его.
     * Если номер с таким физическим номером уже существует - отклоняет добавление.
     * @param room комната для добавления
     * @return true если добавление/обновление успешно, false если номер уже существует
     */
    boolean addRoom(Room room);

    /**
     * Заселяет гостей в номер на указанный период.
     * Обновляет связи между гостями и номером.
     * @param roomNumber номер комнаты для заселения
     * @param guests список гостей для заселения
     * @param checkInDate дата заселения
     * @param checkOutDate дата выселения
     * @return true если заселение успешно, false если комната не найдена или недоступна
     */
    boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate,
                           LocalDate checkOutDate);

    /**
     * Выселяет всех гостей из указанного номера.
     * Удаляет гостей из репозитория.
     * @param roomNumber номер комнаты для выселения
     * @return true если выселение успешно, false если комната не найдена
     */
    boolean checkOut(int roomNumber);

    /**
     * Помечает номер как находящийся на обслуживании.
     * Номер можно перевести на обслуживание только если он свободен.
     * @param roomNumber номер комнаты
     * @param maintenance true для установки на обслуживание, false для снятия
     */
    void setRoomMaintenance(int roomNumber, boolean maintenance);

    /**
     * Изменяет цену номера.
     * @param roomNumber номер комнаты
     * @param newPrice новая цена за сутки
     */
    void changeRoomPrice(int roomNumber, double newPrice);

    /**
     * Возвращает список комнат, отсортированный по указанному критерию.
     * @param option критерий сортировки комнат
     * @return отсортированный список комнат
     */
    List<Room> getSortedRooms(RoomSortOption option);

    /**
     * Возвращает все комнаты системы.
     * @return список всех комнат
     */
    List<Room> getAllRooms();

    /**
     * Находит комнату по физическому номеру.
     * @param roomNumber физический номер комнаты
     * @return Optional с найденной комнатой или empty если не найдена
     */
    Optional<Room> findRoomByNumber(int roomNumber);

    /**
     * Возвращает список всех свободных и не находящихся на обслуживании номеров.
     * @param option критерий сортировки результатов
     * @return отсортированный список свободных комнат
     */
    List<Room> getFreeRooms(RoomSortOption option);

    /**
     * Подсчитывает количество свободных номеров.
     * @return количество свободных номеров
     */
    int countFreeRooms();

    /**
     * Возвращает список номеров, которые освободятся к указанной дате.
     * Включает уже свободные номера и номера, которые будут свободны к дате.
     * @param date дата для проверки доступности
     * @return список доступных к дате номеров
     */
    List<Room> findRoomsThatWillBeFree(LocalDate date);

    /**
     * Вычисляет полную стоимость проживания в номере по количеству дней.
     * Минимальная продолжительность проживания - 1 день.
     * @param room комната для расчета стоимости
     * @return общая стоимость проживания
     */
    double fullRoomPrice(Room room);
    /**
     * Возвращает историю последних заселений в номер.
     * @param roomNumber номер комнаты
     * @return список строк с историей проживания
     */
    List<String> getRoomHistory(int roomNumber);

    /**
     * Находит комнату по идентификатору.
     * @param id идентификатор комнаты
     * @return Optional с найденной комнатой или empty если не найдена
     */
    Optional<Room> findRoomById(long id);

    /**
     * Метод для синхронизации Id после десериализации
     * */
    void syncIdGen();
}
