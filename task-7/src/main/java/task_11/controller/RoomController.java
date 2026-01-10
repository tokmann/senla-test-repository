package task_11.controller;

import di.Component;
import di.Inject;
import task_11.controller.interfaces.IRoomController;
import task_11.model.Room;
import task_11.service.interfaces.IRoomManager;
import task_11.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления номерами.
 * Отвечает за поиск, добавление, сортировку, получение истории и подсчёт свободных номеров.
 */
@Component
public class RoomController implements IRoomController {

    @Inject
    private IRoomManager roomManager;

    /** Возвращает все номера (по заданной сортировке) */
    @Override
    public List<Room> getAllRooms(RoomSortOption option) {
        return roomManager.getSortedRooms(option);
    }

    /** Возвращает свободные номера */
    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        return roomManager.getFreeRooms(option);
    }

    /** Поиск номеров, которые освободятся к заданной дате */
    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return roomManager.findRoomsThatWillBeFree(date);
    }

    /** Расчет полной стоимости проживания в номере */
    @Override
    public Optional<Double> getFullRoomPrice(int roomNumber) {
        Optional<Room> room = roomManager.findRoomByNumber(roomNumber);
        return room.map(roomManager::fullRoomPrice);
    }

    /** Получение истории гостей номера */
    @Override
    public List<String> getRoomHistory(int roomNumber) {
        return roomManager.getRoomHistory(roomNumber);
    }

    /** Получение подробной информации о номере */
    @Override
    public Optional<Room> getFullRoomInfo(int roomNumber) {
        return roomManager.findRoomByNumber(roomNumber);
    }

    /** Подсчёт свободных комнат */
    @Override
    public int countFreeRooms() {
        return roomManager.countFreeRooms();
    }

    /** Добавление нового номера */
    @Override
    public boolean addRoom(Room room) {
        return roomManager.addRoom(room);
    }

    /** Изменение статуса номера */
    @Override
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        roomManager.setRoomMaintenance(roomNumber, maintenance);
    }
}
