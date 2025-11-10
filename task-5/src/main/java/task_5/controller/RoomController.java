package task_5.controller;

import task_5.model.Room;
import task_5.service.RoomManager;
import task_5.view.ConsoleView;
import task_5.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Контроллер для управления номерами.
 * Отвечает за поиск, добавление, сортировку, получение истории и подсчёт свободных номеров.
 */
public class RoomController {

    private final RoomManager roomManager;
    private final Scanner scanner = new Scanner(System.in);

    public RoomController(RoomManager roomManager, ConsoleView view) {
        this.roomManager = roomManager;
    }

    /** Возвращает все номера (по заданной сортировке) */
    public List<Room> getAllRooms(RoomSortOption option) {
        return roomManager.getAllRooms(option);
    }

    /** Возвращает свободные номера */
    public List<Room> getFreeRooms(RoomSortOption option) {
        return roomManager.getFreeRooms(option);
    }

    /** Поиск номеров, которые освободятся к заданной дате */
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return roomManager.findRoomsThatWillBeFree(date);
    }

    /** Расчет полной стоимости проживания в номере */
    public Optional<Double> getFullRoomPrice(int roomNumber) {
        Optional<Room> room = roomManager.findRoomByNumber(roomNumber);
        return room.map(roomManager::fullRoomPrice);
    }

    /** Получение истории гостей номера */
    public List<String> getRoomHistory(int roomNumber, int historyLength) {
        return roomManager.getRoomHistory(roomNumber, historyLength);
    }

    /** Получение подробной информации о номере */
    public Optional<Room> getFullRoomInfo(int roomNumber) {
        return roomManager.findRoomByNumber(roomNumber);
    }

    /** Подсчёт свободных комнат */
    public int countFreeRooms() {
        return roomManager.countFreeRooms();
    }

    /** Добавление нового номера */
    public boolean addRoom(int number, int capacity, int stars, double price) {
        Room room = new Room(number, capacity, price, stars);
        return roomManager.addRoom(room);
    }
}
