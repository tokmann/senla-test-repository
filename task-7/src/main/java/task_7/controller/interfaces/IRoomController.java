package task_7.controller.interfaces;

import task_7.model.Room;
import task_7.service.RoomManager;
import task_7.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Интерфейс контроллера для управления номерами.
 * Отвечает за поиск, добавление, сортировку, получение истории и подсчёт свободных номеров.
 */
public interface IRoomController {

    /** Возвращает все номера (по заданной сортировке) */
    List<Room> getAllRooms(RoomSortOption option);

    /** Возвращает свободные номера */
    List<Room> getFreeRooms(RoomSortOption option);

    /** Поиск номеров, которые освободятся к заданной дате */
    List<Room> findRoomsThatWillBeFree(LocalDate date);

    /** Расчет полной стоимости проживания в номере */
    Optional<Double> getFullRoomPrice(int roomNumber);

    /** Получение истории гостей номера */
    List<String> getRoomHistory(int roomNumber, int historyLength);

    /** Получение подробной информации о номере */
    Optional<Room> getFullRoomInfo(int roomNumber);

    /** Подсчёт свободных комнат */
    int countFreeRooms();

    /** Добавление нового номера */
    boolean addRoom(Room room);

    /** Изменение статуса номера */
    void setRoomMaintenance(int roomNumber, boolean maintenance);
}
