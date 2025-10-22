package task_3_4.management;

import task_3_4.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoomManager {
    private List<Room> rooms;

    public RoomManager() {
        this.rooms = new ArrayList<>();
    }

    public void addRoom(int number, String type, double price) {
        if (getRoomByNumber(number).isPresent()) {
            System.out.println("Номер с таким номером уже существует.");
            return;
        }
        rooms.add(new Room(number, type, price));
        System.out.println("Номер " + number + " добавлен успешно.");
    }

    public void checkIn(int roomNumber, String guestName) {
        Optional<Room> roomOpt = getRoomByNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.checkIn(guestName);
            if (room.isOccupied()) {
                System.out.println("Гость " + guestName + " заселен в номер " + roomNumber);
            } else {
                System.out.println("Не удалось заселить гостя в номер " + roomNumber);
            }
        } else {
            System.out.println("Номер " + roomNumber + " не найден.");
        }
    }

    public void checkOut(int roomNumber) {
        Optional<Room> roomOpt = getRoomByNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            String guestName = room.getGuestName();
            room.checkOut();
            System.out.println("Гость " + guestName + " выселен из номера " + roomNumber);
        } else {
            System.out.println("Номер " + roomNumber + " не найден.");
        }
    }

    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        Optional<Room> roomOpt = getRoomByNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setMaintenance(maintenance);
            System.out.println("Статус обслуживания номера " + roomNumber +
                    " изменен на: " + (maintenance ? "на обслуживании" : "доступен"));
        } else {
            System.out.println("Номер " + roomNumber + " не найден.");
        }
    }

    public void changeRoomPrice(int roomNumber, double newPrice) {
        Optional<Room> roomOpt = getRoomByNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setPrice(newPrice);
            System.out.println("Цена номера " + roomNumber + " изменена на: " + newPrice);
        } else {
            System.out.println("Номер " + roomNumber + " не найден.");
        }
    }

    public void displayAllRooms() {
        System.out.println("ВСЕ НОМЕРА: ");
        if (rooms.isEmpty()) {
            System.out.println("Номера отсутствуют");
        } else {
            rooms.forEach(System.out::println);
        }
    }

    private Optional<Room> getRoomByNumber(int number) {
        return rooms.stream()
                .filter(room -> room.getNumber() == number)
                .findFirst();
    }
}