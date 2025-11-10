package task_5.service;

import task_5.model.Guest;
import task_5.model.Room;
import task_5.model.repository.RoomRepository;
import task_5.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервисный слой для управления номерами отеля.
 * Отвечает за бронирование, выселение, установку цен,
 * техническое обслуживание, историю и фильтрацию номеров.
 * Инкапсулирует бизнес-логику, связанную с сущностью {@link Room}.
 */
public class RoomManager {

    private final RoomRepository repository;
    private final GuestManager guestManager;

    public RoomManager(RoomRepository repository, GuestManager guestManager) {
        this.repository = repository;
        this.guestManager = guestManager;
    }

    /**
     * Добавляет номер в систему, если номер с таким номером ещё не существует.
     */
    public boolean addRoom(Room room) {
        Optional<Room> existing = repository.findByNumber(room.getNumber());
        if (existing.isPresent()) return false;
        repository.save(room);
        return true;
    }

    /**
     * Заселяет гостей в номер на указанный период.
     * Обновляет связи между гостями и номером.
     */
    public boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        Optional<Room> optionalRoom = repository.findByNumber(roomNumber);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            boolean ok = room.checkIn(guests, checkInDate, checkOutDate);
            if (!ok) return false;
            guests.forEach(guest -> guest.setGuestRoom(room));
            return true;
        }
        return false;
    }

    /**
     * Выселяет всех гостей из указанного номера и удаляет их из репозитория гостей.
     */
    public boolean checkOut(int roomNumber) {
        Optional<Room> optionalRoom = repository.findByNumber(roomNumber);
        if (optionalRoom.isPresent()) {
            Room room = optionalRoom.get();
            List<Guest> guests = List.copyOf(room.getGuests());
            room.checkOut();
            guests.forEach(guestManager::removeGuest);
            return true;
        }
        return false;
    }

    /**
     * Помечает номер как находящийся на обслуживании.
     */
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        repository.findByNumber(roomNumber).ifPresent(room -> room.setMaintenance(maintenance));
    }

    /**
     * Изменяет цену номера.
     */
    public void changeRoomPrice(int roomNumber, double newPrice) {
        repository.findByNumber(roomNumber).ifPresent(room -> room.setPrice(newPrice));
    }

    public List<Room> getAllRooms(RoomSortOption option) {
        return repository.findAll().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    public Optional<Room> findRoomByNumber(int roomNumber) {
        return repository.findByNumber(roomNumber);
    }

    /**
     * Возвращает список всех свободных и не находящихся на обслуживании номеров.
     */
    public List<Room> getFreeRooms(RoomSortOption option) {
        return repository.findAll().stream()
                .filter(r -> !r.isOccupied() && !r.isUnderMaintenance())
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Подсчитывает количество свободных номеров.
     */
    public int countFreeRooms() {
        return (int) repository.findAll().stream()
                .filter(r -> !r.isOccupied())
                .count();
    }

    /**
     * Возвращает список номеров, которые освободятся к указанной дате.
     */
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        return repository.findAll().stream()
                .filter(r -> !r.isOccupied() ||
                        (r.getCheckOutDate() != null &&
                                (r.getCheckOutDate().isBefore(date) || r.getCheckOutDate().isEqual(date))))
                .collect(Collectors.toList());
    }

    /**
     * Вычисляет полную стоимость проживания в номере по количеству дней.
     */
    public double fullRoomPrice(Room room) {
        if (room.getCheckInDate() == null || room.getCheckOutDate() == null) return 0.0;
        long days = ChronoUnit.DAYS.between(room.getCheckInDate(), room.getCheckOutDate());
        if (days <= 0) days = 1;
        return days * room.getPrice();
    }

    /**
     * Возвращает историю последних заселений в номер (в виде строковых записей).
     */
    public List<String> getRoomHistory(int roomNumber, int historyLength) {
        return repository.findByNumber(roomNumber)
                .map(room -> room.getLastStays(historyLength))
                .orElse(List.of());
    }
}