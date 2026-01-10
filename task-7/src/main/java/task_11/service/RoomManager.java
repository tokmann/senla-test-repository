package task_11.service;

import di.Component;
import di.Inject;
import task_11.db.TransactionManager;
import task_11.db.interfaces.GuestRepository;
import task_11.db.interfaces.StayHistoryRepository;
import task_11.exceptions.ValidationException;
import task_11.exceptions.rooms.RoomNotFoundException;
import task_11.model.Guest;
import task_11.model.Room;
import task_11.db.interfaces.RoomRepository;
import task_11.service.interfaces.IGuestManager;
import task_11.service.interfaces.IRoomManager;
import task_11.view.enums.RoomSortOption;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
            throw e;
        }
    }

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

            String entry = String.format("Checked in %d guests on %s until %s",
                    guests.size(),
                    checkInDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                    checkOutDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            stayHistoryRepository.addEntry(room.getId(), entry);

            return true;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean checkOut(int roomNumber) {
        transactionManager.beginTransaction();
        try {
            Room room = roomRepository.findByNumber(roomNumber)
                    .orElseThrow(() -> new RoomNotFoundException(roomNumber));

            if (!room.isOccupied()) {
                transactionManager.rollbackTransaction();
                return false;
            }

            room.checkOut();
            roomRepository.save(room);

            String entry = String.format("Checked out all guests on %s",
                    LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
            stayHistoryRepository.addEntry(room.getId(), entry);

            transactionManager.commitTransaction();
            return true;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

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
            throw e;
        }
    }

    @Override
    public void changeRoomPrice(int roomNumber, double newPrice) {
        if (newPrice < 0) {
            throw new ValidationException("Room price cannot be negative");
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
            throw e;
        }
    }

    @Override
    public List<Room> getSortedRooms(RoomSortOption option) {
        return getAllRooms().stream()
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

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
            throw e;
        }
    }

    @Override
    public Optional<Room> findRoomByNumber(int roomNumber) {
        transactionManager.beginTransaction();
        try {
            Optional<Room> roomOpt = roomRepository.findByNumber(roomNumber);
            roomOpt.ifPresent(room -> {
                roomRepository.loadGuestsForRoom(room);
                room.setStayHistory(stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize()));
            });
            transactionManager.commitTransaction();
            return roomOpt;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Room> getFreeRooms(RoomSortOption option) {
        return getAllRooms().stream()
                .filter(room -> !room.isOccupied() && !room.isUnderMaintenance())
                .sorted(option.getComparator())
                .collect(Collectors.toList());
    }

    @Override
    public int countFreeRooms() {
        transactionManager.beginTransaction();
        try {
            int count = roomRepository.countFree();
            transactionManager.commitTransaction();
            return count;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    @Override
    public List<Room> findRoomsThatWillBeFree(LocalDate date) {
        if (date == null) {
            throw new ValidationException("Date cannot be null");
        }

        return getAllRooms().stream()
                .filter(room -> !room.isOccupied() ||
                        (room.getCheckOutDate() != null && !room.getCheckOutDate().isAfter(date)))
                .collect(Collectors.toList());
    }

    @Override
    public double fullRoomPrice(Room room) {
        if (room == null) {
            throw new ValidationException("Room cannot be null");
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
            throw e;
        }
    }

    @Override
    public Optional<Room> findRoomById(long id) {
        transactionManager.beginTransaction();
        try {
            Optional<Room> roomOpt = roomRepository.findById(id);
            roomOpt.ifPresent(room -> {
                roomRepository.loadGuestsForRoom(room);
                room.setStayHistory(stayHistoryRepository.findByRoomId(room.getId(), room.getHistorySize()));
            });
            transactionManager.commitTransaction();
            return roomOpt;
        } catch (Exception e) {
            transactionManager.rollbackTransaction();
            throw e;
        }
    }

    private void validateRoom(Room room) {
        if (room.getNumber() <= 0) {
            throw new ValidationException("Room number must be positive");
        }
        if (room.getCapacity() <= 0) {
            throw new ValidationException("Room capacity must be positive");
        }
        if (room.getPrice() < 0) {
            throw new ValidationException("Room price cannot be negative");
        }
        if (room.getStars() < 1 || room.getStars() > 5) {
            throw new ValidationException("Room stars must be between 1 and 5");
        }
    }

    private void validateCheckIn(List<Guest> guests, LocalDate checkIn, LocalDate checkOut) {
        if (guests == null || guests.isEmpty()) {
            throw new ValidationException("Guest list cannot be empty");
        }
        if (checkIn == null || checkOut == null) {
            throw new ValidationException("Dates cannot be null");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new ValidationException("Check-out must be after check-in");
        }
    }

    private void updateExistingRoom(Room existing, Room newData) {
        existing.setPrice(newData.getPrice());
        if (!existing.isOccupied()) {
            existing.setMaintenance(newData.isUnderMaintenance());
        }
    }
}