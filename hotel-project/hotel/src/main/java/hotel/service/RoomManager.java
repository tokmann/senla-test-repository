package hotel.service;

import di.Component;
import di.Inject;
import hotel.db.TransactionManager;
import hotel.db.interfaces.GuestRepository;
import hotel.db.interfaces.StayHistoryRepository;
import hotel.exceptions.ValidationException;
import hotel.exceptions.rooms.RoomException;
import hotel.exceptions.rooms.RoomNotFoundException;
import hotel.model.Guest;
import hotel.model.Room;
import hotel.db.interfaces.RoomRepository;
import hotel.service.interfaces.IRoomManager;
import hotel.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Менеджер для управления комнатами отеля.
 * Содержит бизнес-логику для добавления, заселения, выселения гостей,
 * управления статусами и историей комнат.
 */
@Component
public class RoomManager implements IRoomManager {

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private GuestRepository guestRepository;

    @Inject
    private StayHistoryRepository stayHistoryRepository;

    @Inject
    private TransactionManager transactionManager;

    /**
     * Добавляет новую комнату в систему.
     * @param room комната для добавления
     * @return true, если комната успешно добавлена, false если комната с таким номером уже существует
     */
    @Override
    public boolean addRoom(Room room) {
        validateRoom(room);

        transactionManager.beginTransaction();
        try {
            if (room.getId() != 0) {
                Optional<Room> existing = roomRepository.findById(room.getId());
                if (existing.isPresent()) {
                    updateExistingRoom(existing.get(), room);
                    transactionManager.commitTransaction();
                    return true;
                }
            }

            Optional<Room> existingByNumber = roomRepository.findByNumber(room.getNumber());
            if (existingByNumber.isPresent()) {
                transactionManager.rollbackTransaction();
                return false;
            }

            roomRepository.save(room);
            transactionManager.commitTransaction();
            return true;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomException("Ошибка при добавлении комнаты", e);
        }
    }

    /**
     * Заселяет гостей в указанную комнату на заданный период.
     * @param roomNumber номер комнаты
     * @param guests список гостей для заселения
     * @param checkInDate дата заселения
     * @param checkOutDate дата выселения
     * @return true, если заселение успешно, false в противном случае
     */
    @Override
    public boolean checkIn(int roomNumber, List<Guest> guests, LocalDate checkInDate, LocalDate checkOutDate) {
        validateCheckIn(guests, checkInDate, checkOutDate);

        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));

            if (room.isUnderMaintenance() || room.isOccupied()) {
                return false;
            }

            if (guests.size() > room.getCapacity()) {
                return false;
            }

            boolean success = room.checkIn(guests, checkInDate, checkOutDate);
            if (!success) {
                return false;
            }

            roomRepository.save(room);

            for (Guest guest : guests) {
                guest.setRoom(room);
                guest.setRoomId(room.getId());
                guestRepository.save(guest);
            }

            String entry = String.format("Заселено %d гостей с %s по %s",
                    guests.size(),
                    checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            stayHistoryRepository.addEntry(room.getId(), entry);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Ошибка при заселении гостей в комнату", e);
        }
    }

    /**
     * Выселяет всех гостей из указанной комнаты.
     * @param roomNumber номер комнаты
     * @return true, если выселение успешно, false если комната пуста или не найдена
     */
    @Override
    public boolean checkOut(int roomNumber) {

        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));

            if (!room.isOccupied()) {
                return false;
            }

            List<Guest> guests = guestRepository.findByRoomId(room.getId());

            for (Guest guest : guests) {
                guest.setRoom(null);
                guest.setRoomId(null);
                guestRepository.save(guest);
            }

            room.checkOut();
            roomRepository.save(room);

            String entry = String.format("Выселены все гости %s",
                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            stayHistoryRepository.addEntry(room.getId(), entry);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RoomException("Ошибка при выселении гостей из комнаты", e);
        }
    }

    /**
     * Устанавливает статус технического обслуживания для комнаты.
     * @param roomNumber номер комнаты
     * @param maintenance true, если требуется обслуживание, false в противном случае
     */
    @Override
    public void setRoomMaintenance(int roomNumber, boolean maintenance) {
        transactionManager.beginTransaction();
        try {
            roomRepository.findByNumber(roomNumber).ifPresent(room -> {
                room.setMaintenance(maintenance);
                roomRepository.save(room);
            });
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomException("Ошибка при изменении статуса обслуживания комнаты", e);
        }
    }

    /**
     * Изменяет цену комнаты.
     * @param roomNumber номер комнаты
     * @param newPrice новая цена
     */
    @Override
    public void changeRoomPrice(int roomNumber, double newPrice) {
        if (newPrice < 0) {
            throw new ValidationException("Цена комнаты не может быть отрицательной");
        }

        transactionManager.beginTransaction();
        try {
            roomRepository.findByNumber(roomNumber).ifPresent(room -> {
                room.setPrice(newPrice);
                roomRepository.save(room);
            });
            transactionManager.commitTransaction();
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomException("Ошибка при изменении цены комнаты", e);
        }
    }

    /**
     * Возвращает список комнат, отсортированных по указанному критерию.
     * @param option критерий сортировки
     * @return отсортированный список комнат
     */
    @Override
    public List<Room> getSortedRooms(RoomSortOption option) {
        return getAllRooms().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает список всех комнат с загруженными связанными данными.
     * @return список всех комнат
     */
    @Override
    public List<Room> getAllRooms() {
        transactionManager.beginTransaction();
        try {
            List<Room> rooms = roomRepository.findAll();
            rooms.forEach(room -> {
                List<Guest> guests = guestRepository.findByRoomId(room.getId());
                room.setGuests(guests);
                room.setOccupied(!guests.isEmpty());
                room.setStayHistory(stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize()));
            });
            transactionManager.commitTransaction();
            return rooms;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomException("Ошибка при получении списка комнат", e);
        }
    }

    /**
     * Находит комнату по номеру.
     * @param roomNumber номер комнаты
     * @return Optional с комнатой или пустой Optional, если комната не найдена
     */
    @Override
    public Optional<Room> findRoomByNumber(int roomNumber) {
        transactionManager.beginTransaction();
        try {
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            roomOpt.ifPresent(room -> {
                List<Guest> guests = guestRepository.findByRoomId(room.getId());
                room.setGuests(guests);
                room.setOccupied(!guests.isEmpty());
                room.setStayHistory(stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize()));
            });
            transactionManager.commitTransaction();
            return roomOpt;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomNotFoundException(roomNumber);
        }
    }

    /**
     * Возвращает список свободных комнат, отсортированных по указанному критерию.
     * Свободные комнаты - это комнаты, которые не заняты и не находятся на обслуживании.
     * @param option критерий сортировки
     * @return отсортированный список свободных комнат
     */
    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        return getAllRooms().stream()
                .filter(room -> !room.isOccupied() && !room.isUnderMaintenance())
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Возвращает количество свободных комнат.
     * @return количество свободных комнат
     */
    @Override
    public int countFreeRooms() {
        transactionManager.beginTransaction();
        try {
            int count = roomRepository.countFree();
            transactionManager.commitTransaction();
            return count;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomException("Ошибка при подсчете свободных комнат", e);
        }
    }

    /**
     * Находит комнаты, которые будут свободны к указанной дате.
     * Включает уже свободные комнаты и те, которые освободятся к заданной дате.
     * @param date дата
     * @return список комнат, которые будут свободны к указанной дате
     */
    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        if (date == null) {
            throw new ValidationException("Дата не может быть пустой");
        }

        return getAllRooms().stream()
                .filter(room -> !room.isOccupied() ||
                        (room.getCheckOutDate() != null && !room.getCheckOutDate().isAfter(date)))
                .collect(Collectors.toList());
    }

    /**
     * Рассчитывает полную стоимость проживания в комнате.
     * @param room комната
     * @return полная стоимость проживания
     */
    @Override
    public double fullRoomPrice(Room room) {
        if (room == null) {
            throw new ValidationException("Комната не может быть пустой");
        }
        if (room.getCheckInDate() == null || room.getCheckOutDate() == null) {
            return 0.0;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                room.getCheckInDate(),
                room.getCheckOutDate()
        );
        return Math.max(1, days) * room.getPrice();
    }

    /**
     * Возвращает историю проживания для указанной комнаты.
     * @param roomNumber номер комнаты
     * @return история проживания
     */
    @Override
    public List<String> getRoomHistory(int roomNumber) {
        transactionManager.beginTransaction();
        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));
            List<String> history = stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize());
            transactionManager.commitTransaction();
            return history;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw new RoomException("Ошибка при получении истории комнаты", e);
        }
    }

    /**
     * Валидирует данные комнаты перед сохранением.
     * @param room комната для валидации
     */
    private void validateRoom(Room room) {
        if (room.getNumber() <= 0) {
            throw new ValidationException("Номер комнаты должен быть положительным числом");
        }
        if (room.getCapacity() <= 0) {
            throw new ValidationException("Вместимость комнаты должна быть положительным числом");
        }
        if (room.getPrice() < 0) {
            throw new ValidationException("Цена комнаты не может быть отрицательной");
        }
        if (room.getStars() < 1 || room.getStars() > 5) {
            throw new ValidationException("Количество звезд должно быть от 1 до 5");
        }
    }

    /**
     * Валидирует данные для заселения гостей.
     * @param guests список гостей для заселения
     * @param checkIn дата заселения
     * @param checkOut дата выселения
     */
    private void validateCheckIn(List<Guest> guests, LocalDate checkIn, LocalDate checkOut) {
        if (guests == null || guests.isEmpty()) {
            throw new ValidationException("Список гостей не может быть пустым");
        }
        if (checkIn == null || checkOut == null) {
            throw new ValidationException("Даты заселения и выселения не могут быть пустыми");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Дата выселения должна быть после даты заселения");
        }
    }

    /**
     * Обновляет существующую комнату новыми данными.
     * @param existing существующая комната
     * @param newData новые данные для обновления
     */
    private void updateExistingRoom(Room existing, Room newData) {
        existing.setPrice(newData.getPrice());
        if (!existing.isOccupied()) {
            existing.setMaintenance(newData.isUnderMaintenance());
        }
    }
}