package task_4.management;

import task_4.model.Guest;
import task_4.model.Room;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RoomManager {

    private List<Room> rooms;
    private GuestManager guestManager;

    private static final Map<String, Comparator<Room>> SORTING_RULES = Map.of(
            "Цена", Comparator.comparingDouble(Room::getPrice),
            "Вместимость", Comparator.comparingInt(Room::getCapacity),
            "Звезды", Comparator.comparing(Room::getStars)
    );

    public RoomManager(GuestManager guestManager) {
        this.rooms = new ArrayList<>();
        this.guestManager = guestManager;
    }

    public void addRoom(int number, int capacity, double price, int stars) {
        if (getRoomByNumber(number).isPresent()) {
            System.out.println("Номер с таким номером уже существует.");
            return;
        }
        rooms.add(new Room(number, capacity, price, stars));
        System.out.println("Номер " + number + " добавлен успешно.");
    }

    public void checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        Optional<Room> roomOpt = getRoomByNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.checkIn(guests, checkInDate, checkOutDate);
            if (room.isOccupied()) {
                System.out.println("Гость(и) " + guests + " заселен(ы) в номер " + roomNumber);
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
            List<Guest> guests = new ArrayList<>(room.getGuests());
            room.checkOut();
            guestManager.removeGuests(guests);
            System.out.println("Гость " + guests + " выселен из номера " + roomNumber);
        } else {
            System.out.println("Номер " + roomNumber + " не найден.");
        }
    }

    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        Optional<Room> roomOpt = getRoomByNumber(roomNumber);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            if (room.isOccupied()) {
                System.out.println("Номер занят");
                return;
            }
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

    public void displayAllSortedRooms(String by) {
        System.out.println("ВСЕ НОМЕРА (ПО " + by + ")");
        Comparator<Room> comparator = SORTING_RULES.get(by);
        if (comparator == null) {
            throw new IllegalArgumentException("Неизвестный критерий сортировки: " + by);
        }
        if (rooms.isEmpty()) {
            System.out.println("Номера отсутствуют");
        } else {
            System.out.println(rooms
                    .stream()
                    .sorted(comparator)
                    .toList());
        }
    }

    public void displayAllFreeSortedRooms(String by) {
        System.out.println("СВОБОДНЫЕ НОМЕРА (ПО " + by + ")");
        Comparator<Room> comparator = SORTING_RULES.get(by);
        if (comparator == null) {
            throw new IllegalArgumentException("Неизвестный критерий сортировки: " + by);
        }
        if (rooms.isEmpty()) {
            System.out.println("Номера отсутствуют");
        } else {
            System.out.println(rooms
                    .stream()
                    .filter(room -> !room.isOccupied() && !room.isUnderMaintenance())
                    .sorted(comparator)
                    .toList());
        }
    }

    public Optional<Room> getRoomByNumber(int number) {
        return rooms.stream()
                .filter(room -> room.getNumber() == number)
                .findFirst();
    }

    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return rooms.stream()
                .filter(room -> !room.isOccupied() ||
                        (room.isOccupied() && room.getCheckOutDate().isBefore(date) ||
                                room.getCheckOutDate().isEqual(date)))
                .toList();
    }

    public int countFreeRooms() {
        return (int) rooms
                .stream()
                .filter(room -> !room.isOccupied())
                .count();
    }

    public double fullRoomPrice(Room room) {
        if (room.getCheckInDate() == null || room.getCheckOutDate() == null) return 0.0;
        long days = ChronoUnit.DAYS.between(room.getCheckInDate(), room.getCheckOutDate());
        if (days <= 0) days = 1;
        return days * room.getPrice();
    }

    public String showHistory(int roomNumber, int historyLength) {
        return getRoomByNumber(roomNumber)
                .map(room -> {
                    List<String> history = room.getLastStays(historyLength);
                    if (history.isEmpty()) {
                        return "История заездов отсутствует для номера " + roomNumber;
                    }
                    return "Последние " + history.size() + " заезда(ов) номера " + roomNumber + ":\n"
                            + String.join("\n", history);
                })
                .orElse("Номер " + roomNumber + " не найден.");
    }

    public void showFullRoomInfo(int roomNumber) {
        Optional<Room> optionalRoom = getRoomByNumber(roomNumber);
        if (optionalRoom.isEmpty()) {
            System.out.println("Номер " + roomNumber + " не найден.");
            return;
        }
        Room room = optionalRoom.get();
        System.out.println("===Полная информация о номере===");
        System.out.println(room);
        if (room.isOccupied()) {
            System.out.println("Номер занят с " + room.getCheckInDate() + " до " + room.getCheckOutDate());
        }
    }
}