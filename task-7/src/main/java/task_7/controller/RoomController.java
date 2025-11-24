package task_7.controller;

import task_7.controller.interfaces.IRoomController;
import task_7.model.Room;
import task_7.service.RoomManager;
import task_7.service.interfaces.IRoomManager;
import task_7.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Контроллер для управления номерами.
 * Отвечает за поиск, добавление, сортировку, получение истории и подсчёт свободных номеров.
 */
public class RoomController implements IRoomController {

    private final IRoomManager roomManager;

    public RoomController(IRoomManager roomManager) {
        this.roomManager = roomManager;
    }

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
    public List<String> getRoomHistory(int roomNumber, int historyLength) {
        return roomManager.getRoomHistory(roomNumber, historyLength);
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
