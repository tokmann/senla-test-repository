package task_5.controller;

import task_5.model.Room;
import task_5.service.RoomManager;
import task_5.view.ConsoleView;
import task_5.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class RoomController {

    private final RoomManager roomManager;
    private final Scanner scanner = new Scanner(System.in);

    public RoomController(RoomManager roomManager, ConsoleView view) {
        this.roomManager = roomManager;
    }

    // Все номера
    public List<Room> getAllRooms(RoomSortOption option) {
        return roomManager.getAllRooms(option);
    }

    // Свободные номера
    public List<Room> getFreeRooms(RoomSortOption option) {
        return roomManager.getFreeRooms(option);
    }

    // Номера, которые будут свободны к дате
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return roomManager.findRoomsThatWillBeFree(date);
    }

    // Полная цена за номер
    public Optional<Double> getFullRoomPrice(int roomNumber) {
        Optional<Room> room = roomManager.findRoomByNumber(roomNumber);
        return room.map(roomManager::fullRoomPrice);
    }

    // История последних гостей
    public List<String> getRoomHistory(int roomNumber, int historyLength) {
        return roomManager.getRoomHistory(roomNumber, historyLength);
    }

    // Подробности номера
    public Optional<Room> getFullRoomInfo(int roomNumber) {
        return roomManager.findRoomByNumber(roomNumber);
    }

    // Количество свободных комнат
    public int countFreeRooms() {
        return roomManager.countFreeRooms();
    }

    // Добавить комнату
    public boolean addRoom(int number, int capacity, int stars, double price) {
        Room room = new Room(number, capacity, price, stars);
        return roomManager.addRoom(room);
    }
}
